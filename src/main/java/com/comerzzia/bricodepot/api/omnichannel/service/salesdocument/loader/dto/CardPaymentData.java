package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto;

public class CardPaymentData {

    private final String cardNumber;
    private final String authorisationCode;
    private final String terminalId;

    public CardPaymentData(String cardNumber, String authorisationCode, String terminalId) {
        this.cardNumber = cardNumber;
        this.authorisationCode = authorisationCode;
        this.terminalId = terminalId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public String getTerminalId() {
        return terminalId;
    }
}
