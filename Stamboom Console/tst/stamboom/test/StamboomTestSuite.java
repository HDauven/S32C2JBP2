package stamboom.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import stamboom.domain.DomeinTest;
import stamboom.domain.StamboomTest;
import stamboom.storage.DatabaseTest;
import stamboom.storage.SerialisatieTest;

/**
 *
 * @author frankpeeters
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    DomeinTest.class,
    StamboomTest.class,
    SerialisatieTest.class,
    DatabaseTest.class})
public class StamboomTestSuite {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(StamboomTestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        if (result.wasSuccessful()) {
            System.out.println("all tests were succesful");
        } else {
            System.out.println("test suite was not succesful");
        }
    }
}
