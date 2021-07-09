package kim.sihwan.daangn.service.product;

import kim.sihwan.daangn.domain.member.Member;
import kim.sihwan.daangn.domain.product.Product;
import kim.sihwan.daangn.domain.product.ProductLike;
import kim.sihwan.daangn.domain.product.ProductTag;
import kim.sihwan.daangn.dto.common.PagingDto;
import kim.sihwan.daangn.dto.common.Result;
import kim.sihwan.daangn.dto.product.ProductListResponseDto;
import kim.sihwan.daangn.dto.product.ProductRequestDto;
import kim.sihwan.daangn.dto.product.ProductResponseDto;
import kim.sihwan.daangn.dto.product.ProductUpdateRequestDto;
import kim.sihwan.daangn.exception.customException.AlreadyGoneException;
import kim.sihwan.daangn.exception.customException.NotMineException;
import kim.sihwan.daangn.exception.customException.OverSizeException;
import kim.sihwan.daangn.repository.member.MemberRepository;
import kim.sihwan.daangn.repository.product.ProductLikeRepository;
import kim.sihwan.daangn.repository.product.ProductQueryRepository;
import kim.sihwan.daangn.repository.product.ProductRepository;
import kim.sihwan.daangn.service.push.RabbitService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductTagService tagService;
    private final ProductLikeService likeService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductQueryRepository queryRepository;
    private final ProductAlbumService productAlbumService;
    private final ProductLikeRepository productLikeRepository;

    private final RabbitService rabbitService;
    private final RedisTemplate<String, String> redisTemplate;

    @Getter
    @Setter
    @AllArgsConstructor
    static class setMemberAndLikeProductListForPaging {
        Member member;
        Long memberId;
        List<ProductLike> likeList;
    }


    public Member findMemberByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findMemberByUsername(username);
    }

    public boolean isLike(List<ProductLike> likeList, Long memberId, Long productId) {
        boolean flag = false;

        for (ProductLike like : likeList) {
            if (like.getMember().getId().equals(memberId) && like.getProduct().getId().equals(productId)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean isLike(String username, Long productId) {
        boolean flag = false;
        if (productLikeRepository.findByMemberUsernameAndProductId(username, productId).isPresent()) {
            flag = true;
        }
        return flag;
    }

    public setMemberAndLikeProductListForPaging getMemberAndLikeProductListForPaging() {
        Member member = findMemberByUsername();
        List<ProductLike> likeList = productLikeRepository.findAllByMemberNickname(member.getNickname());
        return new setMemberAndLikeProductListForPaging(member, member.getId(), likeList);
    }

    @Transactional
    public void addProduct(ProductRequestDto productRequestDto) {
        Product product = productRequestDto.toEntity(productRequestDto);
        if (productRequestDto.getTags().size() > 3) {
            throw new OverSizeException("tag");
        }

        if (productRequestDto.getHasImages().equals("yes")) {
            product = productAlbumService.addProductAlbums(productRequestDto);
        }

        Member member = memberRepository.findMemberByNickname(productRequestDto.getNickname()).orElseThrow(NoSuchElementException::new);
        product.addMember(member);
        productRepository.save(product);
        tagService.addProductTag(product, productRequestDto.getTags());

        Set<ProductTag> productTags = product.getProductTags();
        productTags.forEach(tags -> rabbitService.rabbitProducer(tags.getTag().getTag(), tags.getProduct().getId()));
    }

    public ProductResponseDto findById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(AlreadyGoneException::new);
        return ProductResponseDto.toDto(product, isLike(findMemberByUsername().getUsername(), product.getId()));
    }

    public Result findAllProductByNickname(String nickname, int page) {
        setMemberAndLikeProductListForPaging getData = getMemberAndLikeProductListForPaging();

        List<ProductListResponseDto> list = queryRepository.findProducts(page, 20, nickname).stream()
                .map(product -> getProductListResponseDto(getData.likeList, product, getData.memberId))
                .collect(Collectors.toList());
        int totalPage = productRepository.productCountByNickname(nickname) / 20;

        return new Result(list, totalPage);
    }

    public ProductListResponseDto getProductListResponseDto(List<ProductLike> likeList, Product product, Long memberId) {
        if (isLike(likeList, memberId, product.getId())) {
            return ProductListResponseDto.toDto(product, true);
        }
        return ProductListResponseDto.toDto(product, false);
    }

    public List<ProductListResponseDto> findAllLikeProductByNickname(String nickname) {

        List<ProductLike> likeList = likeService.findAllByNickname(nickname);
        return likeList.stream()
                .map(m -> ProductListResponseDto.toDto(m.getProduct(), true))
                .collect(Collectors.toList());
    }

    public Result paging(int page, List<String> categories, String nickname) {

        ListOperations<String, String> vo = redisTemplate.opsForList();

        setMemberAndLikeProductListForPaging getData = getMemberAndLikeProductListForPaging();

        PagingDto pagingDto = new PagingDto(getData.getMember(), vo, categories);

        List<ProductListResponseDto> list = queryRepository.findProducts(page, 20, nickname).stream()
                .filter(product -> pagingDto.getAreaList().contains(product.getArea()) && pagingDto.getCategories().contains(product.getCategory()) && !pagingDto.getBlockList().contains(product.getNickname()))
                .map(product -> getProductListResponseDto(getData.likeList, product, getData.memberId))
                .collect(Collectors.toList());
        int totalPage = productRepository.productCount() / 20;

        return new Result(list, totalPage);
    }

    @Transactional
    public void setStatus(Long productId, String status) {
        Product product = productRepository.findById(productId).orElseThrow(AlreadyGoneException::new);

        if (status.equals("SALE")) {
            product.setStatusSale();
        } else if (status.equals("RESERVATION")) {
            product.setStatusReservation();
        } else {
            product.setStatusCompleted();
        }
    }

    @Transactional
    public void updateProduct(ProductUpdateRequestDto updateRequestDto) {
        Product product = productRepository.findById(updateRequestDto.getId()).orElseThrow(AlreadyGoneException::new);
        String nickname = findMemberByUsername().getNickname();
        if (!product.getNickname().equals(nickname)) {
            throw new NotMineException();
        }
        if (!updateRequestDto.getIds().isEmpty()) {
            productAlbumService.deleteImages(product, updateRequestDto.getIds());
        }
        if (updateRequestDto.getHasImages().equals("yes")) {
            productAlbumService.appendImages(product, updateRequestDto.getFiles());
        }
        product.update(updateRequestDto.getTitle(), updateRequestDto.getContent());
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(AlreadyGoneException::new);
        String nickname = findMemberByUsername().getNickname();
        if (!product.getNickname().equals(nickname)) {
            throw new NotMineException();
        }
        productRepository.deleteById(productId);
    }

}
