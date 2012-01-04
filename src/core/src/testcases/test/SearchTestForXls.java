package testcases.test;

import org.testng.annotations.Test;

import com.imaginea.brightest.driver.BaseClass;
import com.imaginea.brightest.driver.TestDriverManager;

public class SearchTestForXls extends BaseClass {
@Test
public void searchTest(){
	TestDriverManager tm = new TestDriverManager(context);
	tm.loadTest("dist/testcases/googlesearch.xls");
	tm.runTest(this.selenium);
}
}
