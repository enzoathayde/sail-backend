package br.java.sail.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "transaction_users",
        indexes = {
                @Index(name = "idx_transaction_users_user_id", columnList = "user_id"),
                @Index(name = "idx_transaction_users_computada", columnList = "computada")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "estabelecimento", length = 255)
    private String estabelecimento;

    @Column(name = "categoria", length = 255)
    private String categoria;

    @Column(name = "metodo_pagamento", length = 64)
    private String metodoPagamento;

    @Column(name = "valor", nullable = false)
    private Long valor;

    @Column(name = "computada", nullable = false)
    private boolean computada;

    @Column(name = "feito_em")
    private LocalDateTime feitoEm;

    @Column(name = "mes_parcela", nullable = false)
    private Integer mesParcela;

    @Column(name = "parcelas_totais", nullable = false)
    private Integer parcelasTotais;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TransactionUser(Long userId, String estabelecimento, String categoria, String metodoPagamento, Long valor, Integer mesParcela, Integer parcelasTotais) {
        this.userId = userId;
        this.estabelecimento = estabelecimento;
        this.categoria = categoria;
        this.metodoPagamento = metodoPagamento;
        this.valor = valor;
        this.computada = false;
        this.mesParcela = mesParcela;
        this.parcelasTotais = parcelasTotais;
        this.createdAt = LocalDateTime.now();
    }
}