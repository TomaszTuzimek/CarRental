package pl.tdelektro.CarRental.Inventory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CarControllerTest {

    private static String DB_NAME = "37931948_tom";
    private static String SECRET_KEY;
    private static String JDBC_URL;
    private static String USER;
    private static String PASSWORD;
    private static String FILE_PATH = "src/test/resources/backup.sql";
    private static String customerToken;
    private static String adminToken;


    @BeforeClass
    public static void warmUp() {
        try {
            FileReader fr = new FileReader("src/test/resources/test.properties");
            BufferedReader br = new BufferedReader(fr);
            SECRET_KEY = br.readLine();
            JDBC_URL = br.readLine();
            USER = br.readLine();
            PASSWORD = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        customerToken = generateJwt("test@test.test");
        adminToken = generateJwt("admin@admin.admin");
        createBackup();
    }

    @AfterClass()
    public static void cleanData(){
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .get("/car/all");
        List<CarDTO> carDTOList = response.jsonPath().getList("$", CarDTO.class);

        for (int i = 0; i < carDTOList.size(); i++) {
            String testRegistration = carDTOList.get(i).getRegistration();
            String testMake = carDTOList.get(i).getMake();
            if (testRegistration.equals("") || testMake.equals("test")) {
                String carPath = "/car/" + carDTOList.get(i).getRegistration();
                RestAssured
                        .given()
                        .header("Authorization", "Bearer " + adminToken)
                        .when()
                        .delete(carPath)
                        .then()
                        .statusCode(204);
            }
        }
        restoreBackup();
    }


    public static String generateJwt(String user) {
        String jwt = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(user)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(signInKey(), SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    private static Key signInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static void createBackup() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("mysqldump --host=" + JDBC_URL
                    + " --user=" + USER
                    + " --password=" + PASSWORD
                    + " --result-file" + FILE_PATH);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup created successfully");
            } else {
                System.out.println("Error during backup");
            }
        } catch (IOException e) {
            System.out.println("No file created");
        } catch (InterruptedException e) {
            System.out.println("No file created");
        }
    }

    public static void restoreBackup() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("mysql --host=" + JDBC_URL
                    + " --user=" + USER
                    + " --password=" + PASSWORD
                    + " " + DB_NAME
                    + " < " + FILE_PATH);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup restored successfully");
            } else {
                System.out.println("Error during restore backup");
            }
        } catch (InterruptedException e) {
            System.out.println("DB not restored from copy");
        } catch (IOException e) {
            System.out.println("DB not restored from copy");
        }
    }

    @Test
    public void getCarWithIdTest() {
        RestAssured
                .given()
                .header("Authorization", "Bearer " + customerToken)
                .log()
                .all()
                .get("/car/5")
                .then()
                .body("model", equalTo("1500 GLE"))
                .statusCode(200);
    }

    @Test
    public void getAvailableCarsTest() {
        Set carSet = RestAssured.given().header("Authorization", "Bearer " + customerToken)
                .log().all()
                .when()
                .get("/car")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject("$", Set.class);

        assertThat(carSet, notNullValue());
    }

    @Test
    public void addNewCarTest() {
        String carJson = """
                {
                "make" : "test",
                "model" : "test",
                "type" : "test",
                "registration" : "RE5PECT",
                "modelYear" : "1900",
                "odeDayCost" : "2",
                "isAvailable" : "true"
                }
                """;
        RestAssured
                .given()
                .log()
                .all()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(carJson)
                .post("/car/addNew")
                .then()
                .statusCode(201);
    }
    @Test
    public void addSecondSameCarTest() {
        String carJson = """
                {
                "make" : "test",
                "model" : "test",
                "type" : "test",
                "registration" : "RE5PECT",
                "modelYear" : "1900",
                "odeDayCost" : "2",
                "isAvailable" : "true"
                }
                """;
        RestAssured
                .given()
                .log()
                .all()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(carJson)
                .post("/car/addNew")
                .then()
                .statusCode(409);
    }
}