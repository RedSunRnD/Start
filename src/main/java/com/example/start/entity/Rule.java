package com.example.start.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", unique = true, nullable = false)
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_text", nullable = false)
    private String productText;

    @Column(name = "rule", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<RuleQuery> rule;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }
    public List<RuleQuery> getRule() { return rule; }
    public void setRule(List<RuleQuery> rule) { this.rule = rule; }

    public static class RuleQuery {
        @JsonProperty("query")
        private String query;

        @JsonProperty("arguments")
        private List<String> arguments;

        @JsonProperty("negate")
        private boolean negate;

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public List<String> getArguments() { return arguments; }
        public void setArguments(List<String> arguments) { this.arguments = arguments; }
        public boolean isNegate() { return negate; }
        public void setNegate(boolean negate) { this.negate = negate; }
    }
}