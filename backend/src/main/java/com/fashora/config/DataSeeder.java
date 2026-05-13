package com.fashora.config;

import com.fashora.entity.*;
import com.fashora.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCategories();
        seedProducts();
        seedAdminUser();
        log.info("✅ Data seeding complete");
    }

    private void seedCategories() {
        if (categoryRepo.count() > 0) return;
        List<Category> cats = Arrays.asList(
            Category.builder().name("Women").slug("women").emoji("👗").productCount(12000).build(),
            Category.builder().name("Men").slug("men").emoji("👔").productCount(8000).build(),
            Category.builder().name("Kids").slug("kids").emoji("🧒").productCount(4500).build(),
            Category.builder().name("Footwear").slug("footwear").emoji("👟").productCount(6000).build(),
            Category.builder().name("Beauty").slug("beauty").emoji("💄").productCount(3000).build(),
            Category.builder().name("Sports").slug("sports").emoji("⚽").productCount(2500).build(),
            Category.builder().name("Accessories").slug("accessories").emoji("👜").productCount(5000).build(),
            Category.builder().name("Home & Living").slug("home").emoji("🏠").productCount(1800).build()
        );
        categoryRepo.saveAll(cats);
        log.info("Categories seeded");
    }

    private void seedProducts() {
        if (productRepo.count() > 0) return;

        List<Product> products = Arrays.asList(
            // Women
            Product.builder().name("Floral Wrap Midi Dress").brand("Zara").description("A beautiful floral print wrap dress perfect for summer.").price(new BigDecimal("2499")).originalPrice(new BigDecimal("4999")).category("women").badge("sale").imageEmoji("👗").stock(50).rating(new BigDecimal("4.5")).reviewCount(2341).sizes(List.of("XS","S","M","L","XL")).colors(List.of("Floral Pink","Floral Blue")).build(),
            Product.builder().name("Linen Wide-Leg Trousers").brand("Mango").description("Comfortable and chic linen trousers for warm days.").price(new BigDecimal("3499")).originalPrice(null).category("women").badge("new").imageEmoji("👖").stock(30).rating(new BigDecimal("4.6")).reviewCount(872).sizes(List.of("S","M","L","XL")).colors(List.of("Beige","White","Black")).build(),
            Product.builder().name("Embroidered Anarkali Suit").brand("Biba").description("Stunning traditional Anarkali with intricate embroidery.").price(new BigDecimal("3999")).originalPrice(new BigDecimal("5999")).category("women").badge("sale").imageEmoji("👘").stock(20).rating(new BigDecimal("4.7")).reviewCount(2109).sizes(List.of("S","M","L","XL","XXL")).colors(List.of("Red","Navy","Emerald")).build(),
            Product.builder().name("Handblock Print Kurta").brand("Fabindia").description("Authentic handblock printed kurta in soft cotton.").price(new BigDecimal("1299")).originalPrice(new BigDecimal("1999")).category("women").badge("sale").imageEmoji("🧣").stock(60).rating(new BigDecimal("4.4")).reviewCount(3120).sizes(List.of("S","M","L","XL")).colors(List.of("Indigo","Rust","Mustard")).build(),
            Product.builder().name("Cropped Denim Jacket").brand("H&M").description("Trendy cropped denim jacket for a cool casual look.").price(new BigDecimal("1999")).originalPrice(new BigDecimal("2999")).category("women").badge("hot").imageEmoji("🧥").stock(45).rating(new BigDecimal("4.3")).reviewCount(1456).sizes(List.of("XS","S","M","L")).colors(List.of("Light Blue","Dark Blue")).build(),
            Product.builder().name("Printed Maxi Skirt").brand("AND").description("Flowy maxi skirt with bohemian print.").price(new BigDecimal("1799")).originalPrice(new BigDecimal("2599")).category("women").badge("new").imageEmoji("👗").stock(35).rating(new BigDecimal("4.5")).reviewCount(987).sizes(List.of("S","M","L","XL")).colors(List.of("Tropical","Boho Blue")).build(),

            // Men
            Product.builder().name("Oversized Cotton Tee").brand("H&M").description("Premium oversized cotton t-shirt for everyday wear.").price(new BigDecimal("799")).originalPrice(new BigDecimal("1299")).category("men").badge("sale").imageEmoji("👕").stock(100).rating(new BigDecimal("4.3")).reviewCount(1820).sizes(List.of("S","M","L","XL","XXL")).colors(List.of("White","Black","Grey","Navy")).build(),
            Product.builder().name("Slim Fit Formal Shirt").brand("Arrow").description("Sharp slim fit formal shirt in premium cotton.").price(new BigDecimal("1699")).originalPrice(new BigDecimal("2499")).category("men").badge("hot").imageEmoji("👔").stock(75).rating(new BigDecimal("4.2")).reviewCount(1543).sizes(List.of("S","M","L","XL","XXL")).colors(List.of("White","Light Blue","Striped")).build(),
            Product.builder().name("Chino Slim Trousers").brand("Van Heusen").description("Versatile chino trousers for smart casual occasions.").price(new BigDecimal("2199")).originalPrice(new BigDecimal("3299")).category("men").badge("hot").imageEmoji("👖").stock(55).rating(new BigDecimal("4.3")).reviewCount(1876).sizes(List.of("28","30","32","34","36")).colors(List.of("Khaki","Navy","Olive")).build(),
            Product.builder().name("Mesh Running Shorts").brand("Puma").description("Lightweight mesh shorts for intense workouts.").price(new BigDecimal("999")).originalPrice(null).category("men").badge("new").imageEmoji("🩳").stock(80).rating(new BigDecimal("4.5")).reviewCount(942).sizes(List.of("S","M","L","XL")).colors(List.of("Black","Royal Blue","Red")).build(),
            Product.builder().name("Linen Casual Shirt").brand("Louis Philippe").description("Breathable linen shirt for casual summer days.").price(new BigDecimal("2499")).originalPrice(new BigDecimal("3499")).category("men").badge("sale").imageEmoji("👕").stock(40).rating(new BigDecimal("4.4")).reviewCount(1120).sizes(List.of("S","M","L","XL","XXL")).colors(List.of("Sky Blue","White","Peach")).build(),
            Product.builder().name("Denim Straight Jeans").brand("Wrangler").description("Classic straight-fit denim jeans, timeless style.").price(new BigDecimal("1899")).originalPrice(new BigDecimal("2799")).category("men").badge("sale").imageEmoji("👖").stock(60).rating(new BigDecimal("4.6")).reviewCount(2340).sizes(List.of("28","30","32","34","36","38")).colors(List.of("Dark Blue","Mid Blue","Black")).build(),

            // Footwear
            Product.builder().name("Air Max 270 React").brand("Nike").description("Iconic Air Max comfort meets React foam cushioning.").price(new BigDecimal("7999")).originalPrice(new BigDecimal("9999")).category("footwear").badge("hot").imageEmoji("👟").stock(25).rating(new BigDecimal("4.8")).reviewCount(4231).sizes(List.of("6","7","8","9","10","11")).colors(List.of("Black/White","Triple White","University Red")).build(),
            Product.builder().name("Ultraboost 22 Sneakers").brand("Adidas").description("Revolutionary Boost cushioning for ultimate energy return.").price(new BigDecimal("8999")).originalPrice(new BigDecimal("11999")).category("footwear").badge("sale").imageEmoji("🥿").stock(18).rating(new BigDecimal("4.7")).reviewCount(5621).sizes(List.of("6","7","8","9","10","11","12")).colors(List.of("Core Black","Cloud White","Solar Red")).build(),
            Product.builder().name("Block Heel Sandals").brand("Aldo").description("Chic block heel sandals perfect for evenings out.").price(new BigDecimal("2999")).originalPrice(null).category("footwear").badge("new").imageEmoji("👡").stock(30).rating(new BigDecimal("4.6")).reviewCount(654).sizes(List.of("5","6","7","8","9")).colors(List.of("Nude","Black","Tan")).build(),
            Product.builder().name("Kolhapuri Chappal").brand("Khadim's").description("Authentic Kolhapuri leather chappal, handcrafted.").price(new BigDecimal("899")).originalPrice(new BigDecimal("1299")).category("footwear").badge("sale").imageEmoji("🩴").stock(90).rating(new BigDecimal("4.4")).reviewCount(3210).sizes(List.of("6","7","8","9","10","11")).colors(List.of("Brown","Tan","Black")).build(),

            // Beauty
            Product.builder().name("Ruby Woo Lipstick").brand("MAC").description("Iconic matte red lipstick that never goes out of style.").price(new BigDecimal("1850")).originalPrice(new BigDecimal("2200")).category("beauty").badge("sale").imageEmoji("💄").stock(200).rating(new BigDecimal("4.9")).reviewCount(7832).sizes(List.of("One Size")).colors(List.of("Ruby Woo","Chili","Velvet Teddy")).build(),
            Product.builder().name("Vitamin C Serum 20%").brand("Dot & Key").description("Brightening Vitamin C serum for glowing skin.").price(new BigDecimal("899")).originalPrice(new BigDecimal("1299")).category("beauty").badge("hot").imageEmoji("✨").stock(150).rating(new BigDecimal("4.7")).reviewCount(4521).sizes(List.of("30ml")).colors(List.of("NA")).build(),
            Product.builder().name("Kajal Kohl Eyeliner").brand("Lakmé").description("Intense black kajal for dramatic eye looks.").price(new BigDecimal("249")).originalPrice(new BigDecimal("349")).category("beauty").badge("sale").imageEmoji("👁️").stock(500).rating(new BigDecimal("4.6")).reviewCount(9870).sizes(List.of("One Size")).colors(List.of("Jet Black")).build(),

            // Sports
            Product.builder().name("Pro Yoga Mat 6mm").brand("Decathlon").description("Non-slip yoga mat with alignment lines.").price(new BigDecimal("1299")).originalPrice(new BigDecimal("1799")).category("sports").badge("sale").imageEmoji("🧘").stock(70).rating(new BigDecimal("4.5")).reviewCount(2134).sizes(List.of("6mm")).colors(List.of("Purple","Teal","Black")).build(),
            Product.builder().name("Running Tracksuit").brand("Reebok").description("Moisture-wicking tracksuit for intense training.").price(new BigDecimal("3499")).originalPrice(new BigDecimal("4999")).category("sports").badge("sale").imageEmoji("🏃").stock(35).rating(new BigDecimal("4.4")).reviewCount(876).sizes(List.of("S","M","L","XL","XXL")).colors(List.of("Navy","Black","Grey")).build(),

            // Accessories
            Product.builder().name("Structured Tote Bag").brand("Charles & Keith").description("Elegant structured tote perfect for work and weekends.").price(new BigDecimal("3999")).originalPrice(null).category("accessories").badge("new").imageEmoji("👜").stock(40).rating(new BigDecimal("4.7")).reviewCount(1234).sizes(List.of("One Size")).colors(List.of("Camel","Black","White")).build(),
            Product.builder().name("Minimal Gold Watch").brand("Titan").description("Sleek minimal watch with gold-tone case.").price(new BigDecimal("4999")).originalPrice(new BigDecimal("6999")).category("accessories").badge("sale").imageEmoji("⌚").stock(20).rating(new BigDecimal("4.8")).reviewCount(2987).sizes(List.of("One Size")).colors(List.of("Gold","Rose Gold","Silver")).build()
        );

        productRepo.saveAll(products);
        log.info("Products seeded: {} items", products.size());
    }

    private void seedAdminUser() {
        if (userRepo.existsByEmail("admin@fashora.com")) return;
        User admin = User.builder()
                .name("Fashora Admin")
                .email("admin@fashora.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build();
        userRepo.save(admin);

        User demo = User.builder()
                .name("Demo User")
                .email("demo@fashora.com")
                .password(passwordEncoder.encode("demo123"))
                .role(User.Role.USER)
                .build();
        userRepo.save(demo);
        log.info("Admin & demo users seeded");
    }
}
