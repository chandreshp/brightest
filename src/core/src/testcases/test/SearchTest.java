package testcases.test;

import org.testng.annotations.Test;

import com.imaginea.brightest.driver.BaseClass;
import com.imaginea.brightest.driver.TestDriverManager;

public class SearchTest extends BaseClass {
@Test
public void searchTest(){
	TestDriverManager tm = new TestDriverManager();
	tm.loadTest("/home/varun/gitbrightest/trunk/help/samples/googlesearch.csv");
	tm.runTest(this.selenium);
}
}
