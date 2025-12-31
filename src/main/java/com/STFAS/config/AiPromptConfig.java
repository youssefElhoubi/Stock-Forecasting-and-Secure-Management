package com.STFAS.config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiPromptConfig {

    private static final String STOCK_ANALYSIS_PROMPT = """
        Tu es un expert en gestion de stocks et logistique internationale.
        Ton rôle est d'analyser les données de vente pour aider à la décision de réapprovisionnement.

        CONTEXTE :
        - Produit : {productName}
        - Entrepôt : {warehouseName}
        - Stock actuel : {currentStock} unités
        - Seuil d'alerte configuré : {alertThreshold} unités

        HISTORIQUE DES VENTES (30 derniers jours) :
        {salesHistory}

        INSTRUCTIONS :
        1. Analyse la tendance des ventes (stables, en hausse, en baisse).
        2. Prédis la quantité totale qui sera vendue durant les 30 prochains jours.
        3. Calcule si le stock actuel couvre ces besoins.
        4. Génère une recommandation (ex: "Commander 150 unités", "Stock suffisant", "URGENT : Rupture imminente").

        CONTRAINTE DE RÉPONSE :
        Tu dois répondre UNIQUEMENT au format JSON brut, sans texte avant ou après.
        
        STRUCTURE JSON ATTENDUE :
        \\{
          "predictedSales": 120,
          "confidence": 85.5,
          "recommendation": "Texte de la recommandation ici"
        \\}
        """;

    @Bean
    public PromptTemplate stockPromptTemplate() {
        return new PromptTemplate(STOCK_ANALYSIS_PROMPT);
    }
}