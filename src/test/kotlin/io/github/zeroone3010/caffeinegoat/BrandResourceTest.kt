package io.github.zeroone3010.caffeinegoat

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test

@QuarkusTest
class BrandResourceTest {

    @Test
    fun testBrandEndpoint() {
        given()
          .`when`().get("/brand")
          .then()
             .statusCode(200)
    }

}
