package com.metafit.service;


import com.metafit.dto.request.member.CreateMemberRequest;
import com.metafit.dto.request.member.RenewMembershipRequest;
import com.metafit.dto.request.member.UpdateMemberRequest;
import com.metafit.dto.response.member.MemberDetailResponse;
import com.metafit.dto.response.member.MemberResponse;
import com.metafit.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {

    /**
     * Create a new member
     * @param request Member creation details
     * @param createdBy Username of the creator
     * @return Created member response
     */
    MemberResponse createMember(CreateMemberRequest request, String createdBy);

    /**
     * Get member by ID with full details
     * @param id Member ID
     * @return Member detail response
     */
    MemberDetailResponse getMemberById(Long id);

    /**
     * Get all members with pagination
     * @param pageable Pagination details
     * @return Page of member responses
     */
    Page<MemberResponse> getAllMembers(Pageable pageable);

    /**
     * Get members by status with pagination
     * @param status Member status
     * @param pageable Pagination details
     * @return Page of member responses
     */
    Page<MemberResponse> getMembersByStatus(Member.MemberStatus status, Pageable pageable);

    /**
     * Search members by name or phone
     * @param query Search query
     * @return List of matching members
     */
    List<MemberResponse> searchMembers(String query);

    /**
     * Get members with membership expiring soon (within 7 days)
     * @return List of expiring members
     */
    List<MemberResponse> getExpiringMembers();

    /**
     * Get expired members
     * @return List of expired members
     */
    List<MemberResponse> getExpiredMembers();

    /**
     * Update member details
     * @param id Member ID
     * @param request Update details
     * @param updatedBy Username of the updater
     * @return Updated member response
     */
    MemberResponse updateMember(Long id, UpdateMemberRequest request, String updatedBy);

    /**
     * Renew member's membership
     * @param request Renewal details
     * @param renewedBy Username of the person renewing
     * @return Updated member response
     */
    MemberResponse renewMembership(RenewMembershipRequest request, String renewedBy);

    /**
     * Delete member (soft delete by changing status)
     * @param id Member ID
     */
    void deleteMember(Long id);

    /**
     * Get count of active members
     * @return Count of active members
     */
    long getActiveMemberCount();

    /**
     * Update member status
     * @param id Member ID
     * @param status New status
     */
    void updateMemberStatus(Long id, Member.MemberStatus status);

    /**
     * Check and update expired memberships (scheduled task)
     */
    void checkAndUpdateExpiredMemberships();
}