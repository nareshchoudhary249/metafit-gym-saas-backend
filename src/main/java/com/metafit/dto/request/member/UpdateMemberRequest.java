package com.metafit.dto.request.member;

import com.metafit.entity.Member;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
    private String emergencyContact;
    private String bloodGroup;
    private String notes;
    private Member.MembershipStatus status;
}