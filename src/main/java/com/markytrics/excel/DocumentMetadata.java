package com.markytrics.excel;

import java.time.LocalDateTime;

/**
 * Represents document metadata from the database
 */
public class DocumentMetadata {
    private String id;
    private String documentCode;
    private String documentName;
    private String versionNumber;
    private LocalDateTime registrationDate;
    private LocalDateTime dateOfIssue;
    private String issuerPerson;
    private String issuerPosition;
    private String subDocumentType;
    private String changeReason;

    // Constructors
    public DocumentMetadata() {}

    public DocumentMetadata(String id, String documentCode, String documentName, String versionNumber,
                          LocalDateTime registrationDate, LocalDateTime dateOfIssue, String issuerPerson,
                          String issuerPosition, String subDocumentType, String changeReason) {
        this.id = id;
        this.documentCode = documentCode;
        this.documentName = documentName;
        this.versionNumber = versionNumber;
        this.registrationDate = registrationDate;
        this.dateOfIssue = dateOfIssue;
        this.issuerPerson = issuerPerson;
        this.issuerPosition = issuerPosition;
        this.subDocumentType = subDocumentType;
        this.changeReason = changeReason;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDocumentCode() { return documentCode; }
    public void setDocumentCode(String documentCode) { this.documentCode = documentCode; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getVersionNumber() { return versionNumber; }
    public void setVersionNumber(String versionNumber) { this.versionNumber = versionNumber; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDateTime getDateOfIssue() { return dateOfIssue; }
    public void setDateOfIssue(LocalDateTime dateOfIssue) { this.dateOfIssue = dateOfIssue; }

    public String getIssuerPerson() { return issuerPerson; }
    public void setIssuerPerson(String issuerPerson) { this.issuerPerson = issuerPerson; }

    public String getIssuerPosition() { return issuerPosition; }
    public void setIssuerPosition(String issuerPosition) { this.issuerPosition = issuerPosition; }

    public String getSubDocumentType() { return subDocumentType; }
    public void setSubDocumentType(String subDocumentType) { this.subDocumentType = subDocumentType; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
}
