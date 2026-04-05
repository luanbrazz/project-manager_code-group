package com.portfolio.entity;

import com.portfolio.enums.MemberRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MemberRoleConverter implements AttributeConverter<MemberRole, String> {

    @Override
    public String convertToDatabaseColumn(MemberRole role) {
        if (role == null) return null;
        return role.getDisplayName();
    }

    @Override
    public MemberRole convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return MemberRole.fromString(dbValue);
    }
}