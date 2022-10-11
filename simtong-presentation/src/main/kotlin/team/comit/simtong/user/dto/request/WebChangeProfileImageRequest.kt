package team.comit.simtong.user.dto.request

import javax.validation.constraints.NotBlank

/**
 *
 * 프로필 사진 변경을 요청하는 WebChangeProfileImageRequest
 *
 * @author Chokyunghyeon
 * @date 2022/10/03
 * @version 1.0.0
 **/
data class WebChangeProfileImageRequest(
    @field:NotBlank
    val profileImagePath: String
)