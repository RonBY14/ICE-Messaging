package com.rchat.utils;

public class ResponseGenerator {

    /*
    public static Response getVerificationProblem(VerificationProblem verificationProblem) {
        Response response = new Response(createHeader("vprob"));

        Element root = response.getMessageRoot();

        Element problem = response.getDocument().createElement("problem");
        problem.setTextContent(verificationProblem.name().toLowerCase());
        root.appendChild(problem);

        response.applyNewLengthCalculation();

        return response;
    }

    public static Response getBadRequestResponse(@NotNull Client recipient) {
        return new Response(createHeader("badrq"), recipient);
    }
*/
}
