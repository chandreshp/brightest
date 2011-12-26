package com.imaginea.brightest.client.test;

import java.util.List;

public interface IReportableTask {

    List<Exception> getWarnings();

    List<Exception> getErrors();

    String getPayload();

    String getStepNumber();

}
