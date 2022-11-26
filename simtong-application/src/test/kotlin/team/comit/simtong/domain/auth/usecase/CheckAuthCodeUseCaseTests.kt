package team.comit.simtong.domain.auth.usecase

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import team.comit.simtong.domain.DomainPropertiesInitialization
import team.comit.simtong.domain.auth.exception.AuthCodeMismatchException
import team.comit.simtong.domain.auth.model.AuthCode
import team.comit.simtong.domain.auth.spi.CommandAuthCodeLimitPort
import team.comit.simtong.domain.auth.spi.QueryAuthCodePort

@Import(DomainPropertiesInitialization::class)
@ExtendWith(SpringExtension::class)
class CheckAuthCodeUseCaseTests {

    @MockBean
    private lateinit var queryAuthCodePort: QueryAuthCodePort

    @MockBean
    private lateinit var commandAuthCodeLimitPort: CommandAuthCodeLimitPort

    private lateinit var checkAuthCodeUseCase: CheckAuthCodeUseCase

    private val email = "test@test.com"

    private val code = "123456"

    private val authCodeStub: AuthCode by lazy {
        AuthCode(
            key = email,
            code = code,
            expirationTime = AuthCode.EXPIRED
        )
    }

    private val differentAuthCodeStub by lazy {
        AuthCode(
            key = email,
            code = "654321",
            expirationTime = AuthCode.EXPIRED
        )
    }

    @BeforeEach
    fun setUp() {
        checkAuthCodeUseCase = CheckAuthCodeUseCase(
            commandAuthCodeLimitPort,
            queryAuthCodePort
        )
    }

    @Test
    fun `이메일 인증 확인 성공`() {
        // given
        given(queryAuthCodePort.queryAuthCodeByEmail(email))
            .willReturn(authCodeStub)

        // when & then
        assertDoesNotThrow {
            checkAuthCodeUseCase.execute(email, code)
        }
    }

    @Test
    fun `이메일 인증 코드 찾지 못함`() {
        // given
        given(queryAuthCodePort.queryAuthCodeByEmail(email))
            .willReturn(null)

        // when & then
        assertThrows<AuthCodeMismatchException> {
            checkAuthCodeUseCase.execute(email, code)
        }
    }

    @Test
    fun `이메일 인증 코드 불 일치`() {
        // given
        given(queryAuthCodePort.queryAuthCodeByEmail(email))
            .willReturn(differentAuthCodeStub)

        // when & then
        assertThrows<AuthCodeMismatchException> {
            checkAuthCodeUseCase.execute(email, code)
        }
    }

}