package com.wellness.wellness_backend.dto;

public class PractitionerDTO {
    private String name;
    private String specialization;
    private String bio;
    private Integer experienceYears; // use Integer to allow null checks
    private String email;

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
