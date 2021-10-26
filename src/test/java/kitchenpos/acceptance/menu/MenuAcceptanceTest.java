package kitchenpos.acceptance.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import kitchenpos.acceptance.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class MenuAcceptanceTest extends AcceptanceTest {
    private MenuGroup savedMenuGroup;
    private MenuProduct menuProduct;
    private MenuGroup savedMenuGroup2;
    private MenuProduct menuProduct2;

    @BeforeEach
    void setUp() {
        MenuGroup recommendation = new MenuGroup();
        recommendation.setName("추천메뉴");
        savedMenuGroup = menuGroupDao.save(recommendation);
        MenuGroup best = new MenuGroup();
        best.setName("최고메뉴");
        savedMenuGroup2 = menuGroupDao.save(best);

        Product chicken = new Product();
        chicken.setName("강정치킨");
        chicken.setPrice(BigDecimal.valueOf(17000));
        Product savedChicken = productDao.save(chicken);
        Product chicken2 = new Product();
        chicken2.setName("간장치킨");
        chicken2.setPrice(BigDecimal.valueOf(17000));
        Product savedChicken2 = productDao.save(chicken2);

        menuProduct = new MenuProduct();
        menuProduct.setProductId(savedChicken.getId());
        menuProduct.setQuantity(2);

        menuProduct2 = new MenuProduct();
        menuProduct2.setProductId(savedChicken2.getId());
        menuProduct2.setQuantity(2);
    }

    @DisplayName("메뉴 등록 성공")
    @Test
    void create() {
        Menu halfHalf = new Menu();
        halfHalf.setName("후라이드+후라이드");
        halfHalf.setPrice(BigDecimal.valueOf(19000));
        halfHalf.setMenuGroupId(savedMenuGroup.getId());
        halfHalf.setMenuProducts(Arrays.asList(menuProduct));

        ResponseEntity<Menu> responseEntity = testRestTemplate.postForEntity(
                "/api/menus",
                halfHalf,
                Menu.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("메뉴 등록 실패 - 가격 부재")
    @Test
    void createByNullPrice() {
        Menu halfHalf = new Menu();
        halfHalf.setName("후라이드+후라이드");
        halfHalf.setMenuGroupId(savedMenuGroup.getId());
        halfHalf.setMenuProducts(Arrays.asList(menuProduct));

        ResponseEntity<Menu> responseEntity = testRestTemplate.postForEntity(
                "/api/menus",
                halfHalf,
                Menu.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("상품 등록 실패 - 가격 0 미만")
    @Test
    void createByNegativePrice() {
        Menu halfHalf = new Menu();
        halfHalf.setName("후라이드+후라이드");
        halfHalf.setPrice(BigDecimal.valueOf(-1));
        halfHalf.setMenuGroupId(savedMenuGroup.getId());
        halfHalf.setMenuProducts(Arrays.asList(menuProduct));

        ResponseEntity<Menu> responseEntity = testRestTemplate.postForEntity(
                "/api/menus",
                halfHalf,
                Menu.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("상품 등록 실패 - 잘못된 그룹 메뉴 아이디")
    @Test
    void createByIncorrectMenuGroupId() {
        Menu halfHalf = new Menu();
        halfHalf.setName("후라이드+후라이드");
        halfHalf.setPrice(BigDecimal.valueOf(19000));
        halfHalf.setMenuGroupId(100L);
        halfHalf.setMenuProducts(Arrays.asList(menuProduct));

        ResponseEntity<Menu> responseEntity = testRestTemplate.postForEntity(
                "/api/menus",
                halfHalf,
                Menu.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("상품 등록 실패 - 잘못된 상품 아이디")
    @Test
    void createByIncorrectProductId() {
        Menu halfHalf = new Menu();
        halfHalf.setName("후라이드+후라이드");
        halfHalf.setPrice(BigDecimal.valueOf(19000));
        halfHalf.setMenuGroupId(savedMenuGroup.getId());
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(100L);
        menuProduct.setQuantity(2);

        halfHalf.setMenuProducts(Arrays.asList(menuProduct));

        ResponseEntity<Menu> responseEntity = testRestTemplate.postForEntity(
                "/api/menus",
                halfHalf,
                Menu.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("메뉴 등록 실패 - 메뉴 가격이 메뉴 상품의 금액 합산보다 큰 경우")
    @Test
    void createByIncorrectPrice() {
        Menu halfHalf = new Menu();
        halfHalf.setName("후라이드+후라이드");
        halfHalf.setPrice(BigDecimal.valueOf(35000));
        halfHalf.setMenuGroupId(savedMenuGroup.getId());
        halfHalf.setMenuProducts(Arrays.asList(menuProduct));

        ResponseEntity<Menu> responseEntity = testRestTemplate.postForEntity(
                "/api/menus",
                halfHalf,
                Menu.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("메뉴 목록 조회")
    @Test
    void list() {
        Menu halfHalf = new Menu();
        halfHalf.setName("후라이드+후라이드");
        halfHalf.setPrice(BigDecimal.valueOf(19000));
        halfHalf.setMenuGroupId(savedMenuGroup.getId());
        halfHalf.setMenuProducts(Arrays.asList(menuProduct));
        Menu halfHalf2 = new Menu();
        halfHalf2.setName("앙념+후라이드");
        halfHalf2.setPrice(BigDecimal.valueOf(19000));
        halfHalf2.setMenuGroupId(savedMenuGroup2.getId());
        halfHalf2.setMenuProducts(Arrays.asList(menuProduct2));

        menuDao.save(halfHalf);
        menuDao.save(halfHalf2);

        ResponseEntity<List> responseEntity = testRestTemplate.getForEntity(
                "/api/menus",
                List.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).hasSize(2);
    }
}