package commerce.commandmodel;

import commerce.Patterns;
import commerce.command.CreateSellerCommand;

class CreateSellerCommandValidator {

    public static void validate(CreateSellerCommand command) {
        if (isCommandValid(command) == false) {
            throw new InvariantViolationException();
        }
    }

    private static boolean isCommandValid(CreateSellerCommand command) {
        return isEmailValid(command.email())
            && isUsernameValid(command.username())
            && isPasswordValid(command.password());
    }

    private static boolean isUsernameValid(String username) {
        return username != null && username.matches(Patterns.USERNAME_REGEX);
    }

    private static boolean isEmailValid(String email) {
        return email != null && email.matches(Patterns.EMAIL_REGEX);
    }

    private static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }
}
