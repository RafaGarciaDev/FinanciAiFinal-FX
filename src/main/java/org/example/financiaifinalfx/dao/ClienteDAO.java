package org.example.financiaifinalfx.dao;

import org.example.financiaifinalfx.model.entities.Cliente;
import org.example.financiaifinalfx.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection conexao;

    public ClienteDAO() {
        conexao = Conexao.conectar(); // Conecta ao banco de dados
        criarTabelaClientes(); // Verifica e cria a tabela se não existir
    }

    // Método para verificar se a tabela clientes já existe
    private boolean tabelaExiste() {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'clientes'";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar se a tabela 'clientes' existe: " + e.getMessage(), e);
        }
        return false;
    }

    // Método para criar a tabela clientes se não existir
    private void criarTabelaClientes() {
        if (tabelaExiste()) {
            System.out.println("Tabela 'clientes' já existe.");
            return;
        }

        String criaTabela = "CREATE TABLE clientes (" +
                "cpf VARCHAR(14) PRIMARY KEY, " + // CPF como chave primária
                "nome VARCHAR(100) NOT NULL, " +
                "renda_mensal DOUBLE NOT NULL)";

        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate(criaTabela);
            System.out.println("Tabela 'clientes' criada com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela 'clientes': " + e.getMessage(), e);
        }
    }

    // Método para verificar se um cliente já existe
    private boolean clienteExiste(String cpf) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cpf = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar se cliente existe: " + e.getMessage(), e);
        }
        return false;
    }

    // Método para validar CPF
    private boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return false;

        // Cálculo do primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (10 - i) * Character.getNumericValue(cpf.charAt(i));
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 > 9) digito1 = 0;

        // Cálculo do segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (11 - i) * Character.getNumericValue(cpf.charAt(i));
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 > 9) digito2 = 0;

        return Character.getNumericValue(cpf.charAt(9)) == digito1 &&
                Character.getNumericValue(cpf.charAt(10)) == digito2;
    }

    // Método para adicionar um cliente
    public void adicionarCliente(Cliente cliente) {
        if (!validarCPF(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF inválido. O CPF deve ter 11 dígitos ou 14 caracteres (com máscara).");
        }

        if (clienteExiste(cliente.getCpf())) {
            System.out.println("Cliente com CPF " + cliente.getCpf() + " já existe no banco de dados.");
            return;
        }

        String sql = "INSERT INTO clientes (cpf, nome, renda_mensal) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());
            stmt.setDouble(3, cliente.getRendaMensal());
            stmt.executeUpdate();
            System.out.println("Cliente adicionado com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar cliente: " + e.getMessage(), e);
        }
    }

    // Método para listar todos os clientes
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getDouble("renda_mensal")
                );
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    // Método para buscar um cliente por CPF
    public Cliente buscarClientePorCpf(String cpf) {
        String sql = "SELECT * FROM clientes WHERE cpf = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getDouble("renda_mensal")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por CPF: " + e.getMessage(), e);
        }
        return null; // Retorna null se o cliente não for encontrado
    }

    // Método para atualizar um cliente
    public void atualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, renda_mensal = ? WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setDouble(2, cliente.getRendaMensal());
            stmt.setString(3, cliente.getCpf());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Atualização falhou, nenhum cliente encontrado com o CPF informado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    // Método para deletar um cliente
    public void deletarCliente(String cpf) {
        String sql = "DELETE FROM clientes WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Nenhum cliente encontrado com o CPF informado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cliente: " + e.getMessage(), e);
        }
    }

    // Método para fechar a conexão
    public void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("Conexão fechada com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fechar a conexão: " + e.getMessage(), e);
        }
    }
}