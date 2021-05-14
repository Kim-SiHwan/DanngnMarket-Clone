package kim.sihwan.daangn.controller;

import kim.sihwan.daangn.dto.product.ProductListResponseDto;
import kim.sihwan.daangn.dto.product.ProductRequestDto;
import kim.sihwan.daangn.dto.product.ProductResponseDto;
import kim.sihwan.daangn.service.product.ProductInterestedService;
import kim.sihwan.daangn.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final ProductInterestedService interestedService;

    @PostMapping
    public void addProduct(@ModelAttribute ProductRequestDto productRequestDto){
        productService.addProduct(productRequestDto);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> setInterested(@PathVariable Long productId){
        return new ResponseEntity<>(interestedService.pushInterest( productId),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductListResponseDto>> getProducts(@RequestParam("list") List<String> categories){
        List<ProductListResponseDto> list = productService.findAllProductsByCategory(categories);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId){
        ProductResponseDto productResponseDto = ProductResponseDto.toDto(productService.findById(productId));
        return new ResponseEntity(productResponseDto,HttpStatus.OK);
    }

    @GetMapping(value = "/download", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        Resource resource = new FileSystemResource("C:\\Users\\김시환\\Desktop\\Git\\DaangnClone\\src\\main\\resources\\static\\images\\" + fileName);
        return new ResponseEntity(resource, HttpStatus.OK);
    }
    @PutMapping("/status/{productId}")
    public ResponseEntity<String> setStatus(@PathVariable Long productId,
                                            @RequestParam("status") String status){
        return new ResponseEntity<>(productService.setStatus(productId,status),HttpStatus.OK);
    }

    //성능 테스트 용 매핑
    @PostMapping("/v2")
    public void test(){
        interestedService.test();
    }

    @PostMapping("/v3")
    public void test2(@ModelAttribute ProductRequestDto productRequestDto){
        productService.addProduct2(productRequestDto);
    }






}
