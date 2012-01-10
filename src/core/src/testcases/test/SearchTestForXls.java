package testcases.test;

import org.testng.annotations.Test;

import com.imaginea.brightest.testNG.BaseClass;
import com.imaginea.brightest.testNG.TestNGTestManager;

public class SearchTestForXls extends BaseClass {
@Test
public void searchTest(){
	TestNGTestManager tm = new TestNGTestManager(context);
	tm.loadTest("dist/testcases/googlesearch.xls");
	tm.runTest(this.selenium);
}
}
