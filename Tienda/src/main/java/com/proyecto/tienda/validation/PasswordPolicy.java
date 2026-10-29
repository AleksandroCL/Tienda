package com.proyecto.tienda.validation;

import java.nio.charset.StandardCharsets;

public final class PasswordPolicy {
    public static final String MESSAGE = "Usa al menos 8 caracteres, una mayúscula, un número y un símbolo (por ejemplo: *, # o /). Máximo 72 bytes UTF-8.";

    private PasswordPolicy() { }

    public static boolean isValid(String password) {
        return password != null && password.codePointCount(0, password.length()) >= 8
                && password.getBytes(StandardCharsets.UTF_8).length <= 72
                && password.codePoints().anyMatch(Character::isUpperCase)
                && password.codePoints().anyMatch(Character::isDigit)
                && password.codePoints().anyMatch(c -> !Character.isLetterOrDigit(c)
                        && !Character.isWhitespace(c) && !Character.isSpaceChar(c) && !Character.isISOControl(c));
    }
}
