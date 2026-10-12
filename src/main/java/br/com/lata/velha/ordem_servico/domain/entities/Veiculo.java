package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import java.util.Objects;

public class Veiculo {

    private Long id;
    private Long proprietarioId;
    private Placa placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private boolean ativo;

    public Veiculo() {
        this.ativo = true;
    }

    public Veiculo(Long id, Long proprietarioId, Placa placa, String marca,
                   String modelo, Integer ano, String cor) {
        this.id = id;
        this.proprietarioId = proprietarioId;
        this.placa = placa;
        setMarca(marca);
        setModelo(modelo);
        setAno(ano);
        setCor(cor);
        this.ativo = true;
    }

    // --- business rules ---

    public void deactivate() {
        this.ativo = false;
    }

    public void activate() {
        this.ativo = true;
    }

    // --- getters e setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(Long proprietarioId) { this.proprietarioId = proprietarioId; }

    public Placa getPlaca() { return placa; }
    public void setPlaca(Placa placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) {
        if (marca == null || marca.isBlank()) throw new IllegalArgumentException("Marca não pode ser vazia");
        this.marca = marca;
    }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) {
        if (modelo == null || modelo.isBlank()) throw new IllegalArgumentException("Modelo não pode ser vazio");
        this.modelo = modelo;
    }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) {
        if (ano == null || ano < 1886 || ano > java.time.Year.now().getValue() + 1)
            throw new IllegalArgumentException("Ano inválido");
        this.ano = ano;
    }

    public String getCor() { return cor; }
    public void setCor(String cor) {
        if (cor == null || cor.isBlank()) throw new IllegalArgumentException("Cor não pode ser vazia");
        this.cor = cor;
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Veiculo) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Veiculo{id=" + id + ", placa=" + placa + ", marca='" + marca + "', ativo=" + ativo + "}"; }
}