package kim.sihwan.daangn.service.product;


import kim.sihwan.daangn.domain.member.Member;
import kim.sihwan.daangn.domain.product.Product;
import kim.sihwan.daangn.domain.product.ProductInterested;
import kim.sihwan.daangn.repository.member.MemberRepository;
import kim.sihwan.daangn.repository.product.InterestedRepository;
import kim.sihwan.daangn.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductInterestedService {
    private final InterestedRepository interestedRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;


    @Transactional
    public String pushInterest(Long productId) {
        String msg = "관심상품에 등록되었습니다.";
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ProductInterested interested = interestedRepository.findByMemberUsernameAndProductId(username, productId);
        if (interested != null) {
            msg = "관심상품에서 삭제되었습니다.";
            removeInterest(interested.getId());
            return msg;
        }
        addInterest(username, productId);
        return msg;
    }

    @Transactional
    public void addInterest(String username, Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(NoSuchElementException::new);
        Member member = memberRepository.findMemberByUsername(username);
        ProductInterested productInterested = new ProductInterested();
        productInterested.addMember(member);
        productInterested.addProduct(product);
        interestedRepository.save(productInterested);
    }

    @Transactional
    public void removeInterest(Long id) {
        interestedRepository.deleteById(id);
    }


    public List<ProductInterested> findAll() {
        return interestedRepository.findAll();
    }

    public List<ProductInterested> findAllByNickname(String nickname){
        return interestedRepository.findAllByMemberNickname(nickname);
    }

}
