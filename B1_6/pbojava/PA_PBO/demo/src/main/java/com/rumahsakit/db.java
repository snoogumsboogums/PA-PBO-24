package com.rumahsakit;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.sql.PreparedStatement;

public class db {
    static Connection CONN;
    static Statement statement;
    static PreparedStatement Pstatement;
    static ResultSet result;

    public static void connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            CONN = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbjava", "root", "");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Gagal Connect");
            e.printStackTrace();
        }
    }

    public static void readDokter() {
        connection();

        try {
            statement = CONN.createStatement();
            result = statement.executeQuery("select * from tbdokter");
            while (result.next()) {
                String kodeDokter = result.getString("kodeDokter");
                String namaDokter = result.getString("namaDokter");
                String spesialis = result.getString("spesialis");
                int gaji = result.getInt("gaji");
                Dokter dr = new Dokter(namaDokter, spesialis, gaji, kodeDokter);
                App.dokterList.add(dr);
            }
        } catch (SQLException e) {
            System.out.println("Gagal");
            e.printStackTrace();
        } finally {
            try {
                if (result != null)
                    result.close();
                if (statement != null)
                    statement.close();
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertDokter(String kodeDokter, String namaDokter, String spesialis, int gaji) {
        connection();

        try {
            Pstatement = CONN.prepareStatement(
                    "insert into tbdokter (kodeDokter, namaDokter, spesialis, gaji) values (?, ?, ?, ?)");
            Pstatement.setString(1, kodeDokter);
            Pstatement.setString(2, namaDokter);
            Pstatement.setString(3, spesialis);
            Pstatement.setInt(4, gaji);
            Pstatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal");
            e.printStackTrace();
        } finally {
            try {
                if (Pstatement != null)
                    Pstatement.close();
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateDokter(String kodeDokter, String columnToUpdate, String newValue) {
        connection();

        try {
            String queryUpdate = "UPDATE tbdokter SET " + columnToUpdate + " = ? WHERE kodeDokter = ?";
            try (PreparedStatement preparedStatementUpdate = CONN.prepareStatement(queryUpdate)) {
                preparedStatementUpdate.setString(1, newValue);
                preparedStatementUpdate.setString(2, kodeDokter);
                int rowsAffected = preparedStatementUpdate.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Data dokter berhasil diubah!");
                } else {
                    System.out.println("Kode dokter tidak ditemukan.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengubah data dokter: " + e.getMessage());
        } finally {
            try {
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteDokter(String kodeDokter) {
        connection();

        try {
            if (!isDokterUsed(kodeDokter)) {
                PreparedStatement preparedStatement = CONN
                        .prepareStatement("DELETE FROM tbdokter WHERE kodeDokter = ?");
                preparedStatement.setString(1, kodeDokter);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Data dokter berhasil dihapus.");
                } else {
                    System.out.println("Kode dokter tidak ditemukan.");
                }
                preparedStatement.close();
            } else {
                System.out.println("Tidak dapat menghapus dokter karena terdapat jadwal terkait.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal");
            e.printStackTrace();
        }
    }

    private static boolean isDokterUsed(String kodeDokter) throws SQLException {
        connection();
        PreparedStatement preparedStatement = CONN
                .prepareStatement("SELECT COUNT(*) AS total FROM tbjadwaldokter WHERE kodeDokter = ?");
        preparedStatement.setString(1, kodeDokter);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int total = resultSet.getInt("total");
        preparedStatement.close();
        return total > 0;
    }

    public static ResultSet getDokterList(Connection connection) throws SQLException {
        connection();
        String selectQuery = "SELECT kodeDokter, namaDokter FROM tbdokter";
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        return selectStatement.executeQuery();
    }

    public static String getJadwalNamaDokter(Connection connection, String kodeDokterPilihan) throws SQLException {
        connection();
        String namaDokter = "";
        String namaDokterQuery = "SELECT namaDokter FROM tbdokter WHERE kodeDokter = ?";
        PreparedStatement namaDokterStatement = connection.prepareStatement(namaDokterQuery);
        namaDokterStatement.setString(1, kodeDokterPilihan);
        ResultSet resultSet = namaDokterStatement.executeQuery();
        if (resultSet.next()) {
            namaDokter = resultSet.getString("namaDokter");
        }
        return namaDokter;
    }

    public static void createAkun(Akun akun) throws SQLException {
        connection();

        String query = "INSERT INTO tbakun (username, password) VALUES (?,?)";

        try (PreparedStatement preparedStatement = CONN.prepareStatement(query)) {
            preparedStatement.setString(1, akun.getUsername());
            preparedStatement.setString(2, akun.getPassword());
            preparedStatement.executeUpdate();
        }
    }

    public static Akun getAkunByUsername(String username) throws SQLException {
        connection();
        String query = "SELECT * FROM tbakun WHERE username = ?";
        try (PreparedStatement preparedStatement = CONN.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Akun(resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"));
                }
            }
        }
        return null;
    }

    public static boolean cekUsername(String username) throws SQLException {
        connection();
        String query = "SELECT * FROM tbakun WHERE username = ?";
        try (PreparedStatement preparedStatement = CONN.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    static String getNamaDokter(int idJadwal) {
        connection();
        String namaDokter = null;
        String query = "SELECT tbdokter.namaDokter FROM tbdokter JOIN tbjadwaldokter ON tbdokter.kodeDokter = tbjadwaldokter.kodeDokter WHERE tbjadwaldokter.idJadwal = ?";
        try (PreparedStatement preparedStatement = CONN.prepareStatement(query)) {
            preparedStatement.setInt(1, idJadwal);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    namaDokter = resultSet.getString("namaDokter");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil nama dokter: " + e.getMessage());
        }
        return namaDokter;
    }

    static String generateKodeDokter(Connection connection, String kodeSpesialis) {
        connection();
        String query = "SELECT COUNT(*) AS count FROM tbdokter WHERE spesialis = ?";
        int count = 0;
        try (PreparedStatement preparedStatement = CONN.prepareStatement(query)) {
            preparedStatement.setString(1, kodeSpesialis);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil data: " + e.getMessage());
        }

        String kodeDokter;
        do {

            String nomorUrut = String.format("%02d", count + 1);
            kodeDokter = kodeSpesialis + nomorUrut;

            String checkQuery = "SELECT COUNT(*) AS count FROM tbdokter WHERE kodeDokter = ?";
            try (PreparedStatement checkStatement = CONN.prepareStatement(checkQuery)) {
                checkStatement.setString(1, kodeDokter);
                try (ResultSet checkResult = checkStatement.executeQuery()) {
                    if (checkResult.next()) {
                        int existingCount = checkResult.getInt("count");
                        if (existingCount == 0) {
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error saat memeriksa Kode Dokter: " + e.getMessage());
            }

            count++;
        } while (true);

        return kodeDokter;
    }

    public static void insertJadwalDokter(Connection connection, String kodeDokter, String namaDokter, String spesialis, String tanggal, String jamMulai, String jamSelesai, int kuota) throws SQLException {
        connection();
        String insertSQL = "INSERT INTO tbjadwaldokter (kodeDokter, namaDokter, spesialis, tanggal, jamMulai, jamSelesai, kuota) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, kodeDokter);
            preparedStatement.setString(2, namaDokter);
            preparedStatement.setString(3, spesialis);
            preparedStatement.setString(4, tanggal);
            preparedStatement.setString(5, jamMulai);
            preparedStatement.setString(6, jamSelesai);
            preparedStatement.setInt(7, kuota);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        // Optionally: Add the new Jadwal to the list with the correct ID
                        Jadwal jadwal = new Jadwal(generatedId, kodeDokter, namaDokter, spesialis, tanggal, jamMulai,
                                jamSelesai, kuota);
                        App.jadwalList.add(jadwal);
                    }
                }
            } else {
                System.out.println("Gagal menambahkan jadwal dokter.");
            }
        } catch (SQLException e) {
            System.out.println("Error saat menambahkan jadwal dokter ke database: " + e.getMessage());
            throw e;
        }
    }

    public static String getSpesialisDokter(Connection connection, String kodeDokter) throws SQLException {
        connection();
        String spesialis = "";
        String query = "SELECT spesialis FROM tbdokter WHERE kodeDokter = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, kodeDokter);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    spesialis = resultSet.getString("spesialis");
                }
            }
        }
        return spesialis;
    }

    public static void readJadwal() {
        connection();
        try {
            statement = CONN.createStatement();
            result = statement.executeQuery("SELECT * FROM tbjadwaldokter");
            while (result.next()) {
                int idJadwal = result.getInt("idJadwal");
                String kodeDokter = result.getString("kodeDokter");
                String namaDokter = result.getString("namaDokter");
                String spesialis = result.getString("spesialis"); // Fixed spelling
                String tanggal = result.getString("tanggal");
                String jamMulai = result.getString("jamMulai");
                String jamSelesai = result.getString("jamSelesai");
                int kuota = result.getInt("kuota");
                Jadwal jadwal = new Jadwal(idJadwal, kodeDokter, namaDokter, spesialis, tanggal, jamMulai, jamSelesai,
                        kuota);
                App.jadwalList.add(jadwal);
            }
        } catch (SQLException e) {
            System.out.println("Gagal");
            e.printStackTrace();
        } finally {
            try {
                if (result != null)
                    result.close();
                if (statement != null)
                    statement.close();
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateTanggal(String idJadwalToUpdate, String newValue) {
        connection();
        String updateQuery = "UPDATE tbjadwaldokter SET tanggal = ? WHERE idJadwal = ?";
        try {
            Pstatement = CONN.prepareStatement(updateQuery);
            Pstatement.setString(1, newValue);
            Pstatement.setInt(2, Integer.parseInt(idJadwalToUpdate));
            int rowsUpdated = Pstatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Jadwal dokter berhasil diubah.");
            } else {
                System.out.println("Gagal mengubah jadwal dokter.");
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengubah jadwal dokter: " + e.getMessage());
        } finally {
            try {
                if (Pstatement != null)
                    Pstatement.close();
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateJam(String idJadwalToUpdate, String jamMulai, String jamSelesai) {
        connection();
        String updateQuery = "UPDATE tbjadwaldokter SET jamMulai = ?, jamSelesai = ? WHERE idJadwal = ?";
        try {
            Pstatement = CONN.prepareStatement(updateQuery);
            Pstatement.setString(1, jamMulai);
            Pstatement.setString(2, jamSelesai);
            Pstatement.setInt(3, Integer.parseInt(idJadwalToUpdate));
            int rowsUpdated = Pstatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Jadwal dokter berhasil diubah.");
            } else {
                System.out.println("Gagal mengubah jadwal dokter.");
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengubah jadwal dokter: " + e.getMessage());
        } finally {
            try {
                if (Pstatement != null)
                    Pstatement.close();
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateKuota(String idJadwalToUpdate, int newValue) {
        connection();
        String updateQuery = "UPDATE tbjadwaldokter SET kuota = ? WHERE idJadwal = ?";
        try {
            Pstatement = CONN.prepareStatement(updateQuery);
            Pstatement.setInt(1, newValue);
            Pstatement.setInt(2, Integer.parseInt(idJadwalToUpdate));
            int rowsUpdated = Pstatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Jadwal dokter berhasil diubah.");
            } else {
                System.out.println("Gagal mengubah jadwal dokter.");
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengubah jadwal dokter: " + e.getMessage());
        } finally {
            try {
                if (Pstatement != null)
                    Pstatement.close();
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteJadwalDokter(Connection connection, int idJadwal) {
        connection();
        try {
            if (!isJadwalUsed(idJadwal)) {
                String deleteSQL = "DELETE FROM tbjadwaldokter WHERE idJadwal = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                    preparedStatement.setInt(1, idJadwal);
                    int rowsDeleted = preparedStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Jadwal dokter berhasil dihapus.");
                    } else {
                        System.out.println("Id jadwal tidak ditemukan.");
                    }
                }
            } else {
                System.out.println("Tidak dapat menghapus jadwal dokter karena terdapat jadwal terkait.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isJadwalUsed(int idJadwal) throws SQLException {
        connection(); // Membuka koneksi dengan database
        String query = "SELECT COUNT(*) AS total FROM tbjanjikonsul WHERE idJadwal = ?";
        try (PreparedStatement preparedStatement = CONN.prepareStatement(query)) {
            preparedStatement.setInt(1, idJadwal);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                int total = resultSet.getInt("total");
                return total > 0;
            }
        } finally {
            try {
                if (CONN != null)
                    CONN.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteJanji(int idJanji) {
        connection();
        String deleteSQL = "DELETE FROM tbjanjikonsul WHERE idJanji = ?";
        try (
                PreparedStatement preparedStatement = CONN.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, idJanji);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                // System.out.println("Janji konsultasi berhasil dihapus.");
            } else {
                System.out.println("ID janji tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menghapus janji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void displaySpecialties() {
        connection();
        Set<String> specialties = new HashSet<>();

        specialties.add("umum".toLowerCase());
        specialties.add("gigi".toLowerCase());
        specialties.add("obgyn".toLowerCase());
        specialties.add("mata".toLowerCase());
        specialties.add("bedah".toLowerCase());
        specialties.add("jiwa".toLowerCase());

        String query = "SELECT DISTINCT spesialis FROM tbdokter";
        try (PreparedStatement preparedStatement = CONN.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                specialties.add(resultSet.getString("spesialis").toLowerCase());
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil data spesialis: " + e.getMessage());
        }

        for (String specialty : specialties) {
            System.out.println(specialty.substring(0, 1).toUpperCase() + specialty.substring(1).toLowerCase());
        }
    }

    public static boolean displayAvailableDoctors(String spesialis) {
        connection();
        String query = "SELECT tbjadwaldokter.idJadwal, tbjadwaldokter.kodeDokter, tbdokter.namaDokter, tbjadwaldokter.tanggal, tbjadwaldokter.jamMulai, tbjadwaldokter.jamSelesai, tbjadwaldokter.kuota FROM tbjadwaldokter JOIN tbdokter ON tbjadwaldokter.kodeDokter = tbdokter.kodeDokter WHERE tbdokter.spesialis = ?";
        boolean hasJadwal = false;
        try (PreparedStatement preparedStatement = CONN.prepareStatement(query)) {
            preparedStatement.setString(1, spesialis);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    hasJadwal = true;
                    System.out.println("Dokter yang tersedia untuk spesialis " + spesialis + ":");
                    do {
                        System.out.println("ID Jadwal: " + resultSet.getInt("idJadwal"));
                        System.out.println("Kode Dokter : " + resultSet.getString("kodeDokter"));
                        System.out.println("Nama Dokter : " + resultSet.getString("namaDokter"));
                        System.out.println("Tanggal : " + resultSet.getString("tanggal"));
                        System.out.println("Jam : " + resultSet.getString("jamMulai") + " - "+ resultSet.getString("jamSelesai"));
                        System.out.println("Kuota : " + resultSet.getInt("kuota"));
                        System.out.println("---------------------------------------------------");
                    } while (resultSet.next());
                }
            }
            if (!hasJadwal) {
                System.out.println("Belum ada jadwal yang tersedia untuk spesialis " + spesialis + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil data dokter: " + e.getMessage());
        }
        return hasJadwal;
    }

    public static Object[] getAppointmentDetails(String idJadwal) {
        connection();
        String selectJadwalQuery = "SELECT tanggal, jamMulai, jamSelesai, kuota FROM tbjadwaldokter WHERE idJadwal = ?";
        Object[] appointmentDetails = new Object[4];
        try (PreparedStatement selectStatement = CONN.prepareStatement(selectJadwalQuery)) {
            selectStatement.setInt(1, Integer.parseInt(idJadwal));
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    appointmentDetails[0] = resultSet.getString("tanggal");
                    appointmentDetails[1] = resultSet.getString("jamMulai");
                    appointmentDetails[2] = resultSet.getString("jamSelesai");
                    appointmentDetails[3] = resultSet.getInt("kuota");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil data jadwal: " + e.getMessage());
        }
        return appointmentDetails;
    }

    public static boolean createAppointment(Janji janji, int idPasien, String idJadwal) {
        connection();
        String insertQuery = "INSERT INTO tbjanjikonsul (idJadwal, idPasien, namaDokter, namaPasien, tanggal, jam, keluhan, resep, harga, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = CONN.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, Integer.parseInt(idJadwal));
            insertStatement.setInt(2, idPasien);
            insertStatement.setString(3, janji.getNamaDokter());
            insertStatement.setString(4, janji.getNama());
            insertStatement.setString(5, janji.getTanggal());
            insertStatement.setString(6, janji.getJam());
            insertStatement.setString(7, janji.getKeluhan());
            insertStatement.setString(8, janji.getResep());
            insertStatement.setInt(9, janji.getHarga());
            insertStatement.setString(10, janji.getStatus());

            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {
                decrementKuota(idJadwal);
                System.out.println("Janji temu berhasil dibuat.");
                return true;
            } else {
                System.out.println("Gagal membuat janji temu.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error saat membuat janji temu: " + e.getMessage());
            return false;
        }
    }

    public static boolean createAppointment(int idPasien, String idJadwal, String namaPasien, String tanggal, String jamMulai, String jamSelesai, String keluhanPasien) {
        connection();
        String insertQuery = "INSERT INTO tbjanjikonsul (idJadwal, idPasien, namaDokter, namaPasien, tanggal, jam, keluhan, resep, harga, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = CONN.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, Integer.parseInt(idJadwal));
            insertStatement.setInt(2, idPasien);
            insertStatement.setString(3, db.getNamaDokter(Integer.parseInt(idJadwal)));
            insertStatement.setString(4, namaPasien);
            insertStatement.setString(5, tanggal);
            insertStatement.setString(6, jamMulai + " - " + jamSelesai);
            insertStatement.setString(7, keluhanPasien);
            insertStatement.setString(8, "");
            insertStatement.setInt(9, 0);
            insertStatement.setString(10, "Menunggu Pembayaran");

            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {
                decrementKuota(idJadwal);
                System.out.println("Janji temu berhasil dibuat.");
                return true;
            } else {
                System.out.println("Gagal membuat janji temu.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error saat membuat janji temu: " + e.getMessage());
            return false;
        }
    }

    private static void decrementKuota(String idJadwal) {
        connection();
        String updateKuotaQuery = "UPDATE tbjadwaldokter SET kuota = kuota - 1 WHERE idJadwal = ?";
        try (PreparedStatement updateKuotaStatement = CONN.prepareStatement(updateKuotaQuery)) {
            updateKuotaStatement.setInt(1, Integer.parseInt(idJadwal));
            updateKuotaStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saat mengupdate kuota: " + e.getMessage());
        }
    }

    public static ResultSet selectJanji(int userId) throws SQLException {
        connection();
        String query = "SELECT * FROM tbjanjikonsul WHERE idPasien = ?";
        PreparedStatement preparedStatement = CONN.prepareStatement(query);
        preparedStatement.setInt(1, userId);
        return preparedStatement.executeQuery();
    }

    public static void updateJanji(int idJanji) {
        connection();
        String updateSQL = "UPDATE tbjanjikonsul SET status = 'Selesai' WHERE idJanji = ?";
        try (PreparedStatement preparedStatement = CONN.prepareStatement(updateSQL)) {
            preparedStatement.setInt(1, idJanji);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Konsultasi telah selesai, Terima Kasih!");
                deleteJanji(idJanji); // otomatis hapus klo udah selesai
            } else {
                System.out.println("ID janji tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal memperbarui status janji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updatestatus(int idJanji, String resep, int harga) throws SQLException {
        connection();
        String updateQuery = "UPDATE tbjanjikonsul SET resep = ?, harga = ?, status = 'Menunggu pembayaran' WHERE idJanji = ?";
        try (PreparedStatement updateStatement = CONN.prepareStatement(updateQuery)) {
            updateStatement.setString(1, resep);
            updateStatement.setInt(2, harga);
            updateStatement.setInt(3, idJanji);
            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Penambahan Resep dan Harga Berhasil");
            } else {
                System.out.println("Gagal");
            }
        }
    }

    public static void selectAllJanji() {
        connection();
        String query = "SELECT * FROM tbjanjikonsul";

        try (PreparedStatement preparedStatement = CONN.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("ID Janji: " + resultSet.getInt("idJanji"));
                System.out.println("Nama Pasien: " + resultSet.getString("namaPasien"));
                System.out.println("Tanggal: " + resultSet.getString("tanggal"));
                System.out.println("Jam: " + resultSet.getString("jam"));
                System.out.println("Keluhan: " + resultSet.getString("keluhan"));
                System.out.println("Resep: " + resultSet.getString("resep"));
                System.out.println("Status: " + resultSet.getString("status"));
                System.out.println("----------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}