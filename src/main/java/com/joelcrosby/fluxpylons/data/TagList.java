package com.joelcrosby.fluxpylons.data;

import java.util.List;

public record TagList(List<String> tags) {
    public static TagList Empty = new TagList(List.of());
}
