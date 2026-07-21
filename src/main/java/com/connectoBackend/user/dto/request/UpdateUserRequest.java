package com.connectoBackend.user.dto.request;

import com.connectoBackend.user.enums.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for updating a user profile.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

	@Size(max = 100)
	private String firstName;

	@Size(max = 100)
	private String lastName;

	@Size(min = 3, max = 30)
	@Pattern(regexp = "^[a-zA-Z0-9._]+$")
	private String username;

	@Pattern(regexp = "^[+]?[0-9]{7,15}$")
	private String phoneNumber;

	@Size(max = 500)
	private String profilePictureUrl;

	@Size(max = 500)
	private String bio;

	@Past
	private LocalDate dateOfBirth;

	private Gender gender;
}
