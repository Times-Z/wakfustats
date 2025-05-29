package com.wakfoverlay.domain.logs.model;

public enum ReadMode {
    INITIALIZATION,  // Pour reset/ouverture fichier - pas d'affichage stats
    INCREMENTAL     // Pour lecture continue - affichage stats
}

