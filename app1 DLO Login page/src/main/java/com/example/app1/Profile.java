package com.example.app1;

/**
 * Represents a user profile for a specific service.
 */
public class Profile {
    private int id;
    private String profileName;
    private String profileUsername;
    private String profilePassword;

    /**
     * Constructor to initialize a Profile object.
     *
     * @param id               The unique ID of the profile.
     * @param profileName      The name of the profile.
     * @param profileUsername  The username associated with the profile.
     * @param profilePassword  The password associated with the profile.
     */
    public Profile(int id, String profileName, String profileUsername, String profilePassword) {
        this.id = id;
        this.profileName = profileName;
        this.profileUsername = profileUsername;
        this.profilePassword = profilePassword;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileUsername() {
        return profileUsername;
    }

    public void setProfileUsername(String profileUsername) {
        this.profileUsername = profileUsername;
    }

    public String getProfilePassword() {
        return profilePassword;
    }

    public void setProfilePassword(String profilePassword) {
        this.profilePassword = profilePassword;
    }

    @Override
    public String toString() {
        return profileName;
    }
}
