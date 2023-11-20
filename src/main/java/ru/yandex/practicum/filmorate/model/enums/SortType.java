package ru.yandex.practicum.filmorate.model.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortType {
    YEAR("YEAR"),
    LIKES("LIKES");

    private final String type;

    @Override
    public String toString() {
        return type;
    }

    public static SortType getSortTypeByString(String sortType) {
        for (SortType currentType : values()) {
            if (sortType.toUpperCase().equals(currentType.toString())) {
                return currentType;
            }
        }
        return null;
    }
}
