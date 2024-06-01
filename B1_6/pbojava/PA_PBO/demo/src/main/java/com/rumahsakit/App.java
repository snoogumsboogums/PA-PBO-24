package com.rumahsakit;

import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

public class App {
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    static ArrayList<Akun> akunList = new ArrayList<>();
    static ArrayList<Dokter> dokterList = new ArrayList<>();
    static ArrayList<Jadwal> jadwalList = new ArrayList<>();
    static ArrayList<Janji> janjiList = new ArrayList<>();
    private static Connection connection;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123";

    static public void cls() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/dbjava", "root", "");
    }

    private static int loggedInUserId;

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    public App(Connection connection) {
        App.connection = connection;

    }

    public static void regis(BufferedReader reader) {
        try {
            String username, password;

            System.out.print("Masukkan username: ");
            username = reader.readLine();

            if (db.cekUsername(username)) {
                System.out.println("Username sudah terdaftar.");
                return;
            }

            System.out.print("Masukkan password: ");
            password = reader.readLine();

            Akun account = new Akun(0, username, password);
            akunList.add(account);
            db.createAkun(account);
            System.out.println("Akun berhasil ditambahkan!");
        } catch (IOException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void login(BufferedReader reader) throws IOException, SQLException {
        System.out.print("Masukkan Username: ");
        String username = reader.readLine();
        System.out.print("Masukkan Password: ");
        String password = reader.readLine();

        Akun user = db.getAkunByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            setLoggedInUserId(user.getId());

            System.out.println("Login berhasil.");
            if (username.equals(ADMIN_USERNAME)) {
                adminMenu(reader);
            } else {
                userMenu(reader, username, loggedInUserId);
            }
        } else {
            System.out.println("Login gagal. Username atau password salah.");
        }
    }

    public static void akunAdmin(BufferedReader reader) throws IOException {
        System.out.print("Masukkan Username: ");
        String username = reader.readLine();
        System.out.print("Masukkan Password: ");
        String password = reader.readLine();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("Login sebagai Admin berhasil.");
            adminMenu(reader);
        } else {
            System.out.println("Login sebagai Admin gagal. Username atau password salah.");
        }
    }

    public static void akunPasien(App controller, BufferedReader reader) throws IOException {
        System.out.println("1. Registrasi");
        System.out.println("2. Login");
        System.out.print("Masukkan Pilihan: ");
        int choice = Integer.parseInt(reader.readLine());

        if (choice == 1) {
            regis(reader);
        } else if (choice == 2) {
            try {
                login(reader);
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Pilihan tidak valid.");
        }
    }

    public static void kelolaDokter(BufferedReader br, Connection connection) throws IOException {
        while (true) {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║         Data Dokter            ║");
            System.out.println("║   1. Tambah Data Dokter        ║");
            System.out.println("║   2. Lihat Data Dokter         ║");
            System.out.println("║   3. Ubah Data Dokter          ║");
            System.out.println("║   4. Hapus Data Dokter         ║");
            System.out.println("║   5. Kembali                   ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.print("Input: ");
            String pilih = br.readLine();

            if ("1".equals(pilih)) {
                cls();
                tambahDataDokter(br, connection);
            } else if ("2".equals(pilih)) {
                cls();
                lihatDataDokter(connection);
            } else if ("3".equals(pilih)) {
                cls();
                ubahDataDokter(br, connection);
            } else if ("4".equals(pilih)) {
                cls();
                hapusDataDokter(br, connection);
            } else if ("5".equals(pilih)) {
                break;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void tambahDataDokter(BufferedReader br, Connection connection) throws IOException {
        String namaDokter;
        while (true) {
            System.out.print("Masukkan Nama Dokter: ");
            namaDokter = br.readLine().trim();

            if (namaDokter.isEmpty()) {
                System.out.println("Nama dokter tidak boleh kosong. Silakan coba lagi.");
            } else if (!namaDokter.matches("[a-zA-Z ]+")) {
                System.out.println("Nama dokter hanya boleh mengandung huruf dan spasi. Silakan coba lagi.");
            } else {
                break;
            }
        }
        String spesialis;
        String gelar = "";
        String kodeSpesialis;
        while (true) {
            System.out.print("Masukkan Spesialis (umum/gigi/obgyn/mata/bedah/jiwa): ");
            spesialis = br.readLine();
            if (spesialis.equalsIgnoreCase("umum")) {
                kodeSpesialis = "U";
                gelar = "dr. ";
                break;
            } else if (spesialis.equalsIgnoreCase("gigi")) {
                kodeSpesialis = "G";
                gelar = "drg. ";
                break;
            } else if (spesialis.equalsIgnoreCase("obgyn")) {
                kodeSpesialis = "O";
                gelar = " Sp.Og ";
                break;
            } else if (spesialis.equalsIgnoreCase("mata")) {
                kodeSpesialis = "M";
                gelar = " Sp.M ";
                break;
            } else if (spesialis.equalsIgnoreCase("bedah")) {
                kodeSpesialis = "B";
                gelar = " Sp.B ";
                break;
            } else if (spesialis.equalsIgnoreCase("jiwa")) {
                kodeSpesialis = "J";
                gelar = " Sp.Kj ";
                break;
            } else {
                System.out.println("Spesialis tidak valid. Silakan coba lagi.");
            }
        }
        int gaji = 0;
        while (true) {
            System.out.print("Masukkan Gaji Dokter: ");
            String input = br.readLine();

            if (input.isEmpty()) {
                System.out.println("Gaji dokter tidak boleh kosong. Silakan coba lagi.");
            } else if (!input.matches("\\d+")) {
                System.out.println("Gaji dokter harus berupa angka. Silakan coba lagi.");
            } else {
                gaji = Integer.parseInt(input);
                break;
            }
        }
        String kodeDokter = db.generateKodeDokter(connection, kodeSpesialis);

        String namaLengkapDokter;
        if (spesialis.equalsIgnoreCase("umum") || spesialis.equalsIgnoreCase("gigi")) {
            namaLengkapDokter = gelar + namaDokter;
        } else {
            namaLengkapDokter = namaDokter + gelar;
        }

        Dokter dokter = new Dokter(namaLengkapDokter, spesialis, gaji, kodeDokter);
        dokterList.add(dokter);
        db.insertDokter(kodeDokter, namaLengkapDokter, spesialis, gaji);
        System.out.println("Data dokter berhasil ditambahkan!");
    }

    private static void lihatDataDokter(Connection connection) {
        dokterList.clear();
        db.readDokter();
        for (Dokter dokter : dokterList) {
            dokter.tampilDetail();
        }
    }

    private static void ubahDataDokter(BufferedReader br, Connection connection) throws IOException {
        lihatDataDokter(connection);

        System.out.print("Masukkan Kode Dokter yang ingin diubah: ");
        String kodeDokter = br.readLine();
        System.out.println("Pilih opsi untuk mengubah data dokter:");
        System.out.println("1. Nama Dokter");
        System.out.println("2. Gaji");
        System.out.print("Input: ");
        String opsi = br.readLine();

        String columnToUpdate;
        if ("1".equals(opsi)) {
            columnToUpdate = "namaDokter";
        } else if ("2".equals(opsi)) {
            columnToUpdate = "gaji";
        } else {
            System.out.println("Input tidak valid.");
            return;
        }

        String newValue;
        while (true) {
            System.out.print("Masukkan nilai baru: ");
            newValue = br.readLine();

            if ("1".equals(opsi)) {
                if (!newValue.matches("[a-zA-Z ]+")) {
                    System.out.println("Nama dokter hanya boleh mengandung huruf dan spasi. Silakan coba lagi.");
                    continue;
                }
            } else if ("2".equals(opsi)) {
                if (!newValue.matches("\\d+")) {
                    System.out.println("Gaji dokter harus berupa angka. Silakan coba lagi.");
                    continue;
                }
            }
            break;
        }

        String querySelect = "SELECT spesialis FROM tbdokter WHERE kodeDokter = ?";
        try (PreparedStatement preparedStatementSelect = connection.prepareStatement(querySelect)) {
            preparedStatementSelect.setString(1, kodeDokter);
            ResultSet resultSetSelect = preparedStatementSelect.executeQuery();
            if (resultSetSelect.next()) {
                String spesialisDokter = resultSetSelect.getString("spesialis");

                String gelarDokter;
                if (spesialisDokter.equalsIgnoreCase("umum")) {
                    gelarDokter = "dr. ";
                } else if (spesialisDokter.equalsIgnoreCase("gigi")) {
                    gelarDokter = "drg. ";
                } else if (spesialisDokter.equalsIgnoreCase("obgyn")) {
                    gelarDokter = " Sp.Og ";
                } else if (spesialisDokter.equalsIgnoreCase("mata")) {
                    gelarDokter = " Sp.M ";
                } else if (spesialisDokter.equalsIgnoreCase("bedah")) {
                    gelarDokter = " Sp.B ";
                } else if (spesialisDokter.equalsIgnoreCase("jiwa")) {
                    gelarDokter = " Sp.Kj ";
                } else {
                    System.out.println("Spesialis tidak valid.");
                    return;
                }

                String namaBaru;
                if (columnToUpdate.equals("namaDokter")) {
                    if (spesialisDokter.equalsIgnoreCase("umum") || spesialisDokter.equalsIgnoreCase("gigi")) {
                        namaBaru = gelarDokter + newValue;
                    } else {
                        namaBaru = newValue + gelarDokter;
                    }
                } else {
                    namaBaru = newValue;
                }

                db.updateDokter(kodeDokter, columnToUpdate, namaBaru);

            } else {
                System.out.println("Kode dokter tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil data dokter: " + e.getMessage());
        }
    }

    private static void hapusDataDokter(BufferedReader br, Connection connection) throws IOException {
        lihatDataDokter(connection);
        try {
            System.out.print("Masukkan Kode dokter yang ingin dihapus: ");
            String kodeDokter = br.readLine();
            db.deleteDokter(kodeDokter);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void kelolaJadwalDokter(BufferedReader br, Connection connection) throws IOException {
        while (true) {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║         Jadwal Dokter          ║");
            System.out.println("║   1. Tambah Jadwal Dokter      ║");
            System.out.println("║   2. Lihat Jadwal Dokter       ║");
            System.out.println("║   3. Ubah Jadwal Dokter        ║");
            System.out.println("║   4. Hapus Jadwal Dokter       ║");
            System.out.println("║   5. Kembali                   ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.print("Input: ");
            String pilih = br.readLine();
            if ("1".equals(pilih)) {
                cls();
                tambahJadwalDokter(br, connection);
            } else if ("2".equals(pilih)) {
                cls();
                lihatJadwalDokter(connection);
            } else if ("3".equals(pilih)) {
                cls();
                ubahJadwalDokter(br, connection);
            } else if ("4".equals(pilih)) {
                cls();
                hapusJadwalDokter(br, connection);
            } else if ("5".equals(pilih)) {
                break;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void tambahJadwalDokter(BufferedReader br, Connection connection) throws IOException {
        System.out.println("Daftar Dokter:");
        try {
            ResultSet dokterResultSet = db.getDokterList(connection);
            while (dokterResultSet.next()) {

                String kodeDokter = dokterResultSet.getString("kodeDokter");
                String namaDokter = dokterResultSet.getString("namaDokter");
                System.out.println(kodeDokter + ". " + namaDokter);
            }

            System.out.print("Pilih kode dokter: ");
            String kodeDokterPilihan = br.readLine();

            String namaDokter = db.getJadwalNamaDokter(connection, kodeDokterPilihan);
            String spesialis = db.getSpesialisDokter(connection, kodeDokterPilihan);

            String tanggal = null;
            while (true) {
                System.out.print("Tanggal (YYYY MM DD): ");
                String tanggalInput = br.readLine();
                try {
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy MM dd");
                    DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate parsedDate = LocalDate.parse(tanggalInput, inputFormatter);
                    LocalDate currentDate = LocalDate.now();
                    if (parsedDate.getYear() != 2024) {
                        System.out.println("Tanggal harus di tahun 2024. Silakan masukkan lagi.");
                        continue;
                    }
                    if (parsedDate.isBefore(currentDate)) {
                        System.out.println("Tanggal dan bulan yang dimasukkan sudah lewat. Silakan masukkan lagi.");
                        continue;
                    }
                    tanggal = parsedDate.format(dbFormatter);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Tanggal tidak valid. Silakan masukkan lagi.");
                }
            }

            String jamMulai = null;
            String jamSelesai = null;
            while (true) {
                System.out.print("Jam (HH.MM-HH.MM): ");
                String jamInput = br.readLine();
                String[] jamParts = jamInput.split("-");
                if (jamParts.length != 2) {
                    System.out.println("Format jam tidak valid. Silakan masukkan dalam format HH.MM-HH.MM.");
                    continue;
                }
                try {
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
                    LocalTime parsedJamMulai = LocalTime.parse(jamParts[0], timeFormatter);
                    LocalTime parsedJamSelesai = LocalTime.parse(jamParts[1], timeFormatter);
                    if (parsedJamSelesai.isBefore(parsedJamMulai)) {
                        System.out.println("Jam selesai tidak boleh sebelum jam mulai. Silakan masukkan lagi.");
                        continue;
                    }
                    jamMulai = jamParts[0];
                    jamSelesai = jamParts[1];
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Jam tidak valid. Silakan masukkan jam dalam format 24 jam (HH.MM-HH.MM).");
                }
            }

            int kuota = 0;
            while (true) {
                System.out.print("Kuota: ");
                try {
                    kuota = Integer.parseInt(br.readLine());
                    if (kuota <= 0) {
                        System.out.println("Kuota harus lebih besar dari 0. Silakan masukkan lagi.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Kuota tidak valid. Silakan masukkan angka.");
                }
            }

            db.insertJadwalDokter(connection, kodeDokterPilihan, namaDokter, spesialis, tanggal, jamMulai, jamSelesai,
                    kuota);
            System.out.println("Jadwal dokter berhasil ditambahkan.");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void lihatJadwalDokter(Connection connection) {
        jadwalList.clear();
        db.readJadwal();
        for (Jadwal jadwal : App.jadwalList) {
            jadwal.tampilDetail();
        }
    }

    private static void ubahJadwalDokter(BufferedReader br, Connection connection) throws IOException {
        lihatJadwalDokter(connection);

        System.out.print("Masukkan ID Jadwal yang ingin diubah: ");
        String idJadwalToUpdate = br.readLine();

        System.out.println("Opsi yang dapat diubah:");
        System.out.println("1. Tanggal");
        System.out.println("2. Jam");
        System.out.println("3. Kuota");
        System.out.print("Pilih opsi: ");
        String opsi = br.readLine();

        if ("1".equals(opsi)) {
            ubahTanggal(br, idJadwalToUpdate);
        } else if ("2".equals(opsi)) {
            ubahJam(br, idJadwalToUpdate);
        } else if ("3".equals(opsi)) {
            ubahKuota(br, idJadwalToUpdate);
        } else {
            System.out.println("Opsi tidak valid. Silakan pilih opsi yang tersedia.");
        }
    }

    private static void ubahTanggal(BufferedReader br, String idJadwalToUpdate) throws IOException {
        while (true) {
            System.out.print("Masukkan nilai baru untuk tanggal (YYYY MM DD): ");
            String newValue = br.readLine();
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy MM dd");
                DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedDate = LocalDate.parse(newValue, inputFormatter);
                LocalDate currentDate = LocalDate.now();
                if (parsedDate.getYear() != 2024) {
                    System.out.println("Tanggal harus di tahun 2024. Silakan masukkan lagi.");
                    continue;
                }
                if (parsedDate.isBefore(currentDate)) {
                    System.out.println("Tanggal yang dimasukkan sudah lewat. Silakan masukkan lagi.");
                    continue;
                }
                newValue = parsedDate.format(dbFormatter);
                db.updateTanggal(idJadwalToUpdate, newValue);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Tanggal tidak valid. Silakan masukkan lagi.");
            }
        }
    }

    private static void ubahJam(BufferedReader br, String idJadwalToUpdate) throws IOException {
        while (true) {
            System.out.print("Masukkan nilai baru untuk jam (HH.MM-HH.MM): ");
            String newValue = br.readLine();
            String[] jamParts = newValue.split("-");
            if (jamParts.length != 2) {
                System.out.println("Format jam tidak valid. Silakan masukkan dalam format HH.MM-HH.MM.");
                continue;
            }
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
                LocalTime parsedJamMulai = LocalTime.parse(jamParts[0], timeFormatter);
                LocalTime parsedJamSelesai = LocalTime.parse(jamParts[1], timeFormatter);
                if (parsedJamSelesai.isBefore(parsedJamMulai)) {
                    System.out.println("Jam selesai tidak boleh sebelum jam mulai. Silakan masukkan lagi.");
                    continue;
                }
                db.updateJam(idJadwalToUpdate, jamParts[0], jamParts[1]);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Jam tidak valid. Silakan masukkan jam dalam format 24 jam (HH.MM-HH.MM).");
            }
        }
    }

    private static void ubahKuota(BufferedReader br, String idJadwalToUpdate) throws IOException {
        while (true) {
            System.out.print("Masukkan nilai baru untuk kuota: ");
            try {
                int newValue = Integer.parseInt(br.readLine());
                if (newValue <= 0) {
                    System.out.println("Kuota harus lebih besar dari 0. Silakan masukkan lagi.");
                    continue;
                }
                db.updateKuota(idJadwalToUpdate, newValue);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Kuota tidak valid. Silakan masukkan angka.");
            }
        }
    }

    private static void hapusJadwalDokter(BufferedReader br, Connection connection) throws IOException {
        lihatJadwalDokter(connection);
        try {
            System.out.print("Masukkan ID Jadwal yang ingin dihapus: ");
            int idJadwal = Integer.parseInt(br.readLine());
            db.deleteJadwalDokter(connection, idJadwal);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void adminMenu(BufferedReader reader) throws IOException {
        while (true) {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║   Selamat Datang Admin!        ║");
            System.out.println("║   [1] Kelola Data Dokter       ║");
            System.out.println("║   [2] Kelola Jadwal Dokter     ║");
            System.out.println("║   [3] Lihat Janji Konsultasi   ║");
            System.out.println("║   [0] Keluar                   ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.print("Pilih: ");
            String menu = reader.readLine();

            if (menu.equals("1")) {
                cls();
                kelolaDokter(reader, connection);
            } else if (menu.equals("2")) {
                cls();
                kelolaJadwalDokter(reader, connection);
            } else if (menu.equals("3")) {
                cls();
                lihatSemuaJanji(reader, connection);
            } else if (menu.equals("0")) {
                System.out.println("Keluar dari menu admin");
                break;
            } else {
                System.out.println("Input Salah");
            }
        }
    }

    private static void userMenu(BufferedReader reader, String username, int userId) throws IOException {
        while (true) {
            db.readDokter();
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║               MENU             ║");
            System.out.println("║   [1] Buat Janji Konsultasi    ║");
            System.out.println("║   [2] Lihat Janji Konsultasi   ║");
            System.out.println("║   [3] Lihat Dokter             ║");
            System.out.println("║   [0] Keluar                   ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.print("Pilih: ");
            String menu = reader.readLine();

            if (menu.equals("1")) {
                cls();
                buatJanji(reader, connection, username);
            } else if (menu.equals("2")) {
                cls();
                lihatJanji(connection, userId, username, reader);
            } else if (menu.equals("3")) {
                cls();
                System.out.println("Daftar Dokter:");
                for (Dokter dokter : dokterList) {
                    dokter.tampilDetail(false);
                }
            } else if (menu.equals("0")) {
                System.out.println("Program telah berakhir");
                break;
            } else {
                System.out.println("Input Salah");
            }
            dokterList.clear();
        }
    }

    public static void buatJanji(BufferedReader reader, Connection connection, String username) throws IOException {
        int idPasien = App.getLoggedInUserId();
        db.displaySpecialties();
        System.out.print("Masukkan Spesialis yang dipilih: ");
        String spesialis = reader.readLine();
        boolean hasAvailableDoctors = db.displayAvailableDoctors(spesialis);
        if (!hasAvailableDoctors) {
            return;
        }

        System.out.print("Masukkan ID Jadwal yang dipilih: ");
        String idJadwal = reader.readLine();

        Object[] appointmentDetails = db.getAppointmentDetails(idJadwal);
        String tanggal = (String) appointmentDetails[0];
        String jamMulai = (String) appointmentDetails[1];
        String jamSelesai = (String) appointmentDetails[2];
        int kuota = (int) appointmentDetails[3];

        if (kuota <= 0) {
            System.out.println("Maaf, kuota untuk jadwal ini sudah habis.");
            return;
        }
        System.out.print("Masukkan Nama Pasien: ");
        String namaPasien = reader.readLine();
        System.out.print("Masukkan Keluhan Pasien: ");
        String keluhanPasien = reader.readLine();

        Janji janji = new Janji(db.getNamaDokter(Integer.parseInt(idJadwal)), namaPasien, tanggal,
                jamMulai + " - " + jamSelesai, keluhanPasien, "", 0, "Menunggu Pembayaran");
        janjiList.add(janji);
        db.createAppointment(idPasien, idJadwal, namaPasien, tanggal, jamMulai, jamSelesai, keluhanPasien);
    }

    public static void lihatJanji(Connection connection, int userId, String username, BufferedReader reader)
    throws IOException {
try {
    ResultSet resultSet = db.selectJanji(userId);
    if (!resultSet.isBeforeFirst()) {
        System.out.println("Tidak ada janji temu yang ditemukan untuk " + username);
    } else {
        System.out.println("Janji temu untuk " + username + ":");
        while (resultSet.next()) {
            Janji janji = new Janji(
                    resultSet.getString("namaDokter"),
                    resultSet.getString("namaPasien"),
                    resultSet.getString("tanggal"),
                    resultSet.getString("jam"),
                    resultSet.getString("keluhan"),
                    resultSet.getString("resep"),
                    resultSet.getInt("harga"),
                    resultSet.getString("status"));
            janji.tampilDetail();
            String resep = resultSet.getString("resep");
            int harga = resultSet.getInt("harga");
            String status = resultSet.getString("status");

            if (!resep.isEmpty() && harga > 0 && "Menunggu pembayaran".equals(status)) {
                System.out.print("Apakah Anda ingin melakukan pembayaran untuk janji ini? (ya/tidak): ");
                String pilihan = reader.readLine();
                if ("ya".equalsIgnoreCase(pilihan)) {
                    System.out.println("Total harga yang harus dibayar: " + harga);
                    db.updateJanji(resultSet.getInt("idJanji"));
                } else if ("tidak".equalsIgnoreCase(pilihan)) {
                    System.out.println("Lakukan pembayaran untuk menebus obat!");
                }
            }
        }
    }
} catch (SQLException e) {
    System.out.println("Error saat mengambil data janji temu: " + e.getMessage());
}
}


    private static void lihatSemuaJanji(BufferedReader reader, Connection connection) throws IOException {
        try {
            db.selectAllJanji();

            System.out.print("Masukkan ID Janji yang ingin Anda beri resep: ");
            int idJanji = Integer.parseInt(reader.readLine());

            System.out.print("Masukkan resep: ");
            String resep = reader.readLine();

            System.out.print("Masukkan total harga: ");
            int harga = Integer.parseInt(reader.readLine());

            db.updatestatus(idJanji, resep, harga);

        } catch (SQLException e) {
            System.out.println("Error saat mengambil data janji konsultasi: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Input harus berupa angka untuk ID Janji dan harga.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    

    public static void main(String[] args) {
        Connection connection = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            connection = getConnection();
            App app = new App(connection);

            while (true) {
                System.out.println("╔════════════════════════════════╗");
                System.out.println("║   Halo, Selamat Datang! ^---^  ║");
                System.out.println("║   1. Admin                     ║");
                System.out.println("║   2. Pasien                    ║");
                System.out.println("║   3. Exit                      ║");
                System.out.println("╚════════════════════════════════╝");
                System.out.print("Masukan pilihan: ");
                try {
                    int choice = Integer.parseInt(reader.readLine());

                    if (choice == 1) {
                        cls();
                        akunAdmin(reader);
                    } else if (choice == 2) {
                        cls();
                        akunPasien(app, reader);
                    } else if (choice == 3) {
                        break;
                    } else {
                        System.out.println("Pilihan tidak valid.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Masukkan hanya menerima input angka.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error saat menutup koneksi: " + e.getMessage());
                }
            }
        }
    }
}