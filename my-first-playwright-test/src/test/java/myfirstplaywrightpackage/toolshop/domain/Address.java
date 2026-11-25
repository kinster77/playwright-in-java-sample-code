package myfirstplaywrightpackage.toolshop.domain;

import net.datafaker.Faker;

public record Address(
        String street,
        String house_number,
        String city,
        String state,
        String country,
        String postal_code){

    public static Address randomAddress(Faker fake){
        return new Address(
                fake.address().streetName(),
                fake.address().buildingNumber(),
                fake.address().city(),
                fake.address().state(),
                fake.address().country(),
                fake.address().postcode()
        );
    }
}

