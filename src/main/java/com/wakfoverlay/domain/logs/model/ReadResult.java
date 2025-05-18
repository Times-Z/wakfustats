package com.wakfoverlay.domain.logs.model;

import java.util.List;

public record ReadResult(FileReadStatus status, List<String> lines) {
}

