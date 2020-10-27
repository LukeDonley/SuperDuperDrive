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

	private String firstNameTest = "John";
	private String lastNameTest = "Doe";
	private String userNameTest = "johndoe";
	private String passwordTest = "P$a9H31?v";

	private String noteTitleTest = "Note #1";
	private String noteDescriptionTest = "This Is A Note";

	private String credentialUrlTest = "www.yahoo.com";
	private String credentialUsernameTest = "user1";
	private String credentialPasswordTest = "drowssap";

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private WebDriverWait wait;
	private JavascriptExecutor javascriptExecutor;

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
		// Verify Home Page Is Not Accessible Without Logging In
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(4)
	public void getHomePage() {
		// Verify Home Page Is Accessible After Signup And Login
		signupUser(firstNameTest, lastNameTest, userNameTest, passwordTest);
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

		fillAndSubmitNoteForm(noteTitleTest, noteDescriptionTest);

		navigateHomeFromResult();
		navigateToTab("nav-notes-tab");

		Assertions.assertDoesNotThrow(() -> {
			driver.findElement(By.xpath("//th[text()='" + noteTitleTest + "']"));
			driver.findElement(By.xpath("//td[text()='" + noteDescriptionTest + "']"));
		});

		logout();
	}

	@Test
	@Order(6)
	public void editNote() {
		login(userNameTest,passwordTest);
		navigateToTab("nav-notes-tab");
		handleCRUD("edit", "Note", "userTable");

		fillAndSubmitNoteForm("New Note Title", "This Is The Updated Note");

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

		fillAndSubmitCredentialForm(credentialUrlTest, credentialUsernameTest, credentialPasswordTest);

		navigateHomeFromResult();
		navigateToTab("nav-credentials-tab");

		Assertions.assertDoesNotThrow(() -> {
			driver.findElement(By.xpath("//th[text()='" + credentialUrlTest + "']"));
			driver.findElement(By.xpath("//td[text()='" + credentialUsernameTest + "']"));
		});

		logout();
	}

	@Test
	@Order(9)
	public void editCredential() {
		login(userNameTest, passwordTest);
		navigateToTab("nav-credentials-tab");
		handleCRUD("edit", "Credential", "credentialTable");

		fillAndSubmitCredentialForm("www.google.com", "differentusername", "12345");

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

	private void navigateToTab(String tabName) {
		WebElement tab = driver.findElement(By.id(tabName));
		javascriptExecutor.executeScript("arguments[0].click()", tab);
	}

//	private void handleNote(String crudType) {
//		if (crudType == "create") {
//			WebElement newNote = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newNote")));
//			newNote.click();
//		}
//		else if (crudType == "edit") {
//			WebElement editNote = driver.findElement(By.xpath("//*[@id='userTable']/tbody/tr/td[1]/button"));
//			wait.until(ExpectedConditions.elementToBeClickable(editNote));
//			editNote.click();
//		} else if (crudType == "delete") {
//			WebElement deleteBtn = driver.findElement(
//					By.xpath("//*[@id=\"userTable\"]/tbody/tr/td[1]/a"));
//			wait.until(ExpectedConditions.elementToBeClickable(deleteBtn));
//			deleteBtn.click();
//		}
//	}

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

	private void fillAndSubmitNoteForm(String noteTitle, String noteDescription) {
		WebElement noteTitleField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
		noteTitleField.clear();
		noteTitleField.sendKeys(noteTitle);
		WebElement noteDescriptionField  = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
		noteDescriptionField.clear();
		noteDescriptionField.sendKeys(noteDescription);
		WebElement submitNote = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveNote")));
		submitNote.click();
	}

	private void fillAndSubmitCredentialForm(String url, String username, String password) {
		WebElement urlField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
		urlField.clear();
		urlField.sendKeys(url);
		WebElement usernameField  = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
		usernameField.clear();
		usernameField.sendKeys(username);
		WebElement passwordField  = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
		passwordField.clear();
		passwordField.sendKeys(password);

		WebElement submitCredential = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveCredential")));
		submitCredential.click();
	}

}
