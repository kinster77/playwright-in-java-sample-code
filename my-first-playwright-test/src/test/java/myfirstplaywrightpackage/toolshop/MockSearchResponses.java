package myfirstplaywrightpackage.toolshop;

public class MockSearchResponses {
    public static final String RESPONSE_WITH_A_SINGLE_ENTRY = """
            
           {
    "current_page": 1,
    "data": [
        {
            "id": "01M0QZXDCHH2KZPHGNHXSD0D6A",
            "name": "Super Pliers",
            "description": "Versatile combination pliers designed for gripping, bending, and cutting wire with ease. Featuring chrome vanadium steel construction with induction-hardened cutting edges, these pliers deliver excellent grip and leverage for a wide range of tasks. The precision-machined jaws combine flat gripping surfaces with a pipe-grip section and integrated wire cutter for true multi-purpose functionality. Ergonomic bi-component handles reduce hand fatigue during extended use and provide a secure hold even with oily or gloved hands. The joint is precisely fitted to eliminate play and ensure smooth operation over thousands of cycles. Ideal for electricians, mechanics, and DIY enthusiasts tackling everyday projects around the workshop or job site.",
            "price": 14.15,
            "is_location_offer": false,
            "is_rental": false,
            "co2_rating": "D",
            "in_stock": true,
            "is_eco_friendly": false,
            "product_image": {
                "id": "01M0QZXDC1KH9MW41FZR2WXAWM",
                "by_name": "Helinton Fantin",
                "by_url": "https://unsplash.com/@fantin",
                "source_name": "Unsplash",
                "source_url": "https://unsplash.com/photos/W8BNwvOvW4M",
                "file_name": "pliers01.avif",
                "title": "Combination pliers"
            },
            "category": {
                "id": "01M0QZXDBKP8TRT3Q6W839EZBT",
                "name": "Pliers"
            },
            "brand": {
                "id": "01M0QZXD1FJ8F5W7Y5SW6YAJJ3",
                "name": "ForgeFlex Tools"
            }
        }
    ],
    "from": 1,
    "last_page": 1,
    "per_page": 9,
    "to": 1,
    "total": 1
}""";

    public static final String RESPONSE_WITH_NO_ENTRIES = """
            {
                "current_page": 1,
                "data": [],
                "from": null,
                "last_page": 1,
                "per_page": 9,
                "to": null,
                "total": 0
            }
            """;
}
