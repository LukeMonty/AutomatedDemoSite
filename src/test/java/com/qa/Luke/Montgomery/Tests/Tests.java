package com.qa.Luke.Montgomery.Tests;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import com.qa.Luke.Montgomery.Pages.AddUser;
import com.qa.Luke.Montgomery.Pages.Home;
import com.qa.Luke.Montgomery.Pages.LoginPage;
import com.qa.Luke.Montgomery.main.*;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class Tests {
	WebDriver driver;

	static ExtentReports reports = new ExtentReports(Constants.REPORT_FILEPATH, true);

	static ExtentTest test;
	static XSSFWorkbook workbook = null;
	static FileInputStream file = null;
	static FileOutputStream outPut = null;

	@BeforeClass
	public static void setUpTests() {

		try {
			file = new FileInputStream(Constants.DATA_FILEPATH + Constants.DATA_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			workbook = new XSSFWorkbook(file);
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		
		try {
			outPut = new FileOutputStream(Constants.DATA_FILEPATH + Constants.DATA_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Before
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", Constants.DRIVER_FILEPATH);
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(Constants.URL);

	}

	@Test
	public void testingLoginUser() throws IOException {

		XSSFSheet sheet = workbook.getSheetAt(0);

		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			test = reports.startTest("Login with user");
			XSSFCell cellUser = sheet.getRow(i).getCell(0);
			String username = cellUser.getStringCellValue();

			XSSFCell cellPassword = sheet.getRow(i).getCell(1);
			String password = cellPassword.getStringCellValue();

			Home homePage = PageFactory.initElements(driver, Home.class);
			homePage.clickAddUserLink();
			test.log(LogStatus.INFO, "Click onto the 'Add User' page");
			AddUser addUser = PageFactory.initElements(driver, AddUser.class);
			addUser.inputNewUser(username, password);
			test.log(LogStatus.INFO, "Save the user: " + username + " with the password: " + password);
			addUser.goToLoginPage();
			test.log(LogStatus.INFO, "Go to Login page");
			LoginPage loginPage = PageFactory.initElements(driver, LoginPage.class);
			loginPage.loginUser(username, password);
			test.log(LogStatus.INFO, "Login with the username: " + username + " and the password: " + password);

			String expectedResult = "**Successful Login**";
			XSSFCell resultCell = sheet.getRow(i).getCell(2);
			if (!expectedResult.equals(loginPage.checkLoginStatus())) {
				test.log(LogStatus.FAIL, "Test has failed: Login not successful");
				resultCell.setCellValue("FAIL");
				

			} else {

				assertEquals(expectedResult, loginPage.checkLoginStatus());
				test.log(LogStatus.PASS, "Login was successful");
				resultCell.setCellValue("PASS");
				
			}
			
			reports.flush();

		}

	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@AfterClass
	public static void tearDownClass() throws IOException {
		file.close();
		workbook.write(outPut);
		outPut.close();
		
	}

}
