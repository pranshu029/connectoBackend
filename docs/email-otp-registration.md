# Email OTP registration API

All endpoints are public and use the existing `ApiResponse`/`ErrorResponse` formats.

## Send OTP

`POST /api/v1/auth/send-otp`

Request:

```json
{"email":"user@example.com"}
```

The response contains no OTP. The code is sent through the configured `spring.mail.username` mail account. The same endpoint supports resend after the configured cooldown and daily limit.

## Verify OTP

`POST /api/v1/auth/verify-otp`

Request:

```json
{"email":"user@example.com","otp":"123456"}
```

Success data contains `email` and a short-lived `registrationToken`. OTPs expire after five minutes by default, and failed attempts are limited.

## Register

`POST /api/v1/users/register`

Use the existing registration fields plus:

```json
{"registrationToken":"..."}
```

The server validates that the token belongs to the submitted email, has not expired, and has not been consumed. It consumes the token before creating the user and sets `emailVerified` to `true`.