package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;
	private WebDriver driver;
	private WebDriverWait wait;
	private JavascriptExecutor javascriptExecutor;
	private String userNameTest = "johndoe";
	private String passwordTest = "P$a9H31?v";

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		this.wait = new WebDriverWait(driver, 10);
		this.javascriptExecutor = (JavascriptExecutor) driver;
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	@Order(1)
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(2)
	public void getSignupPage() {
		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}

	@Test
	@Order(3)
	public void getHomePageNotAuthorized() {
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(4)
	public void getHomePage() {
		// Verify Home Page Is Accessible After Signup And Login
		signupUser("John", "Doe", userNameTest, passwordTest);
		login(userNameTest, passwordTest);
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Home", driver.getTitle());

		// Verify Home Page Is No Longer Accessible After Logout
		logout();
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(5)
	public void createNote() {
		login(userNameTest, passwordTest);
		navigateToTab("nav-notes-tab");
		handleCRUD("create", "Note", "userTable");

		fillAndSubmitForm(
				new String[]{"note-title", "note-description"},
				new String[]{"Note #1", "This Is A Note"}, "Note");

		navigateHomeFromResult();
		navigateToTab("nav-notes-tab");

		Assertions.assertDoesNotThrow(() -> {
			driver.findElement(By.xpath("//th[text()='Note #1']"));
			driver.findElement(By.xpath("//td[text()='This Is A Note']"));
		});

		logout();
	}

	@Test
	@Order(6)
	public void editNote() {
		login(userNameTest,passwordTest);
		navigateToTab("nav-notes-tab");
		handleCRUD("edit", "Note", "userTable");

		fillAndSubmitForm(
				new String[]{"note-title", "note-description"},
				new String[]{"New Note Title", "This Is The Updated Note"}, "Note");

		navigateHomeFromResult();
		navigateToTab("nav-notes-tab");

		Assertions.assertDoesNotThrow(() -> {
			driver.findElement(By.xpath("//th[text()='New Note Title']"));
			driver.findElement(By.xpath("//td[text()='This Is The Updated Note']"));
		});

		logout();
	}

	@Test
	@Order(7)
	public void deleteNote() {
		login(userNameTest,passwordTest);
		navigateToTab("nav-notes-tab");
		handleCRUD("delete", "Note", "userTable");

		navigateHomeFromResult();
		navigateToTab("nav-notes-tab");

		Assertions.assertThrows(NoSuchElementException.class, () -> {
			driver.findElement(By.xpath("//th[text()='New Note Title']"));
			driver.findElement(By.xpath("//td[text()='This Is The Updated Note']"));
		});

		logout();
	}

	@Test
	@Order(8)
	public void createCredential() {
		login(userNameTest, passwordTest);
		navigateToTab("nav-credentials-tab");
		handleCRUD("create", "Credential", "credentialTable");

		fillAndSubmitForm(
				new String[]{"credential-url", "credential-username", "credential-password"},
				new String[]{"www.yahoo.com", "user1", "drowssap"}, "Credential");

		navigateHomeFromResult();
		navigateToTab("nav-credentials-tab");

		Assertions.assertDoesNotThrow(() -> {
			driver.findElement(By.xpath("//th[text()='www.yahoo.com']"));
			driver.findElement(By.xpath("//td[text()='user1']"));
		});

		logout();
	}

	@Test
	@Order(9)
	public void editCredential() {
		login(userNameTest, passwordTest);
		navigateToTab("nav-credentials-tab");
		handleCRUD("edit", "Credential", "credentialTable");

		fillAndSubmitForm(
				new String[]{"credential-url", "credential-username", "credential-password"},
				new String[]{"www.google.com", "differentusername", "12345"}, "Credential");

		navigateHomeFromResult();
		navigateToTab("nav-credentials-tab");

		Assertions.assertDoesNotThrow(() -> {
			driver.findElement(By.xpath("//th[text()='www.google.com']"));
			driver.findElement(By.xpath("//td[text()='differentusername']"));
		});

		logout();
	}

	@Test
	@Order(10)
	public void deleteCredential() {
		login(userNameTest, passwordTest);
		navigateToTab("nav-credentials-tab");
		handleCRUD("delete", "Credential", "credentialTable");

		navigateHomeFromResult();
		navigateToTab("nav-credentials-tab");

		Assertions.assertThrows(NoSuchElementException.class, () -> {
			driver.findElement(By.xpath("//th[text()='www.google.com']"));
			driver.findElement(By.xpath("//td[text()='differentusername']"));
		});

		logout();
	}

	public void signupUser(String firstName, String lastName, String userName, String password) {
		driver.get("http://localhost:" + this.port + "/signup");
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.sendKeys(firstName);
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.sendKeys(lastName);
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.sendKeys(userName);
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.sendKeys(password);
		WebElement signUpButton = driver.findElement(By.id("signup"));
		signUpButton.click();
	}

	public void login(String userName, String password) {
		driver.get("http://localhost:" + this.port + "/login");
		WebElement usernameInput = driver.findElement(By.id("inputUsername"));
		usernameInput.sendKeys(userName);
		WebElement passwordInput = driver.findElement(By.id("inputPassword"));
		passwordInput.sendKeys(password);
		WebElement loginButton = driver.findElement(By.id("submit-button"));
		loginButton.click();
		wait.until(ExpectedConditions.titleContains("Home"));
	}

	public void logout() {
		WebElement logoutButton = driver.findElement(By.id("logout"));
		logoutButton.click();
		wait.until(ExpectedConditions.titleContains("Login"));
		Assertions.assertEquals("Login", driver.getTitle());
	}

	private void navigateToTab(String tabName) {
		WebElement tab = driver.findElement(By.id(tabName));
		javascriptExecutor.executeScript("arguments[0].click()", tab);
	}

	private void handleCRUD(String crudType, String objectType, String tableName) {
		if (crudType == "create") {
			WebElement newObject = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new" + objectType)));
			newObject.click();
		}
		else if (crudType == "edit") {
			WebElement editObject = driver.findElement(By.xpath("//*[@id='" + tableName + "']/tbody/tr/td[1]/button"));
			wait.until(ExpectedConditions.elementToBeClickable(editObject));
			editObject.click();
		} else if (crudType == "delete") {
			WebElement deleteObject = driver.findElement(
					By.xpath("//*[@id='" + tableName + "']/tbody/tr/td[1]/a"));
			wait.until(ExpectedConditions.elementToBeClickable(deleteObject));
			deleteObject.click();
		}
	}

	private void navigateHomeFromResult() {
		wait.until(ExpectedConditions.titleContains("Result"));
		driver.get("http://localhost:" + this.port + "/home");
	}

	private void fillAndSubmitForm(String elementNames[], String values[], String objectType) {
		for(int i=0; i<elementNames.length; i++) {
			WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementNames[i])));
			field.clear();
			field.sendKeys(values[i]);
		}
		WebElement submit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("save" + objectType)));
		submit.click();
	}

}
