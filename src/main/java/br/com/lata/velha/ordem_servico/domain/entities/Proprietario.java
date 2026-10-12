package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.Endereco;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Proprietario {

    private Long id;
    private String nome;
    private String email;
    private Documento documento;
    private NumeroCelular numeroCelular;
    private Endereco endereco;
    private List<Veiculo> veiculos;
    private boolean ativo;

    public Proprietario() {
        this.veiculos = new ArrayList<>();
        this.ativo = true;
    }

    public Proprietario(Long id, String nome, String email, Documento documento,
                        NumeroCelular numeroCelular, Endereco endereco) {
        this.id = id;
        setNome(nome);
        setEmail(email);
        this.documento = documento;
        this.numeroCelular = numeroCelular;
        this.endereco = endereco;
        this.veiculos = new ArrayList<>();
        this.ativo = true;
    }

    // --- business rules ---

    public void deactivate() {
        this.ativo = false;
    }

    public void activate() {
        this.ativo = true;
    }

    public void addVeiculo(Veiculo veiculo) {
        Objects.requireNonNull(veiculo, "Veículo não pode ser nulo");
        this.veiculos.add(veiculo);
    }

    public void removeVeiculo(Veiculo veiculo) {
        this.veiculos.remove(veiculo);
    }

    // --- getters and setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do proprietário não pode ser vazio");
        this.nome = nome;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Email inválido");
        this.email = email;
    }

    public Documento getDocumento() { return documento; }
    public void setDocumento(Documento documento) { this.documento = documento; }

    public NumeroCelular getNumeroCelular() { return numeroCelular; }
    public void setNumeroCelular(NumeroCelular numeroCelular) { this.numeroCelular = numeroCelular; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public List<Veiculo> getVeiculos() { return Collections.unmodifiableList(veiculos); }
    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos != null ? new ArrayList<>(veiculos) : new ArrayList<>();
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Proprietario) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Proprietario{id=" + id + ", nome='" + nome + "', documento=" + documento + ", ativo=" + ativo + "}"; }
}