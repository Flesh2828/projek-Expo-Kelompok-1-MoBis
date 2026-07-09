package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OwnerLaporanController {

    @FXML private BarChart<String, Number> barChartLaporan;
    @FXML private LineChart<String, Number> lineChartLaporan;
    @FXML private TableView<?> tableRingkasan; // Anda bisa membuat model class jika diperlukan

    @FXML
    public void initialize() {
        initBarChart();
        initLineChart();
    }

    private void initBarChart() {
        if (barChartLaporan != null) {
            barChartLaporan.getData().clear();

            XYChart.Series<String, Number> pendapatan = new XYChart.Series<>();
            pendapatan.setName("Pendapatan");
            pendapatan.getData().add(new XYChart.Data<>("Feb", 18.5));
            pendapatan.getData().add(new XYChart.Data<>("Mar", 22.0));
            pendapatan.getData().add(new XYChart.Data<>("Apr", 19.5));
            pendapatan.getData().add(new XYChart.Data<>("Mei", 25.0));
            pendapatan.getData().add(new XYChart.Data<>("Jun", 23.0));
            pendapatan.getData().add(new XYChart.Data<>("Jul", 27.0));

            XYChart.Series<String, Number> pengeluaran = new XYChart.Series<>();
            pengeluaran.setName("Pengeluaran");
            pengeluaran.getData().add(new XYChart.Data<>("Feb", 12.0));
            pengeluaran.getData().add(new XYChart.Data<>("Mar", 14.5));
            pengeluaran.getData().add(new XYChart.Data<>("Apr", 13.0));
            pengeluaran.getData().add(new XYChart.Data<>("Mei", 16.5));
            pengeluaran.getData().add(new XYChart.Data<>("Jun", 15.0));
            pengeluaran.getData().add(new XYChart.Data<>("Jul", 17.5));

            barChartLaporan.getData().addAll(pendapatan, pengeluaran);
        }
    }

    private void initLineChart() {
        if (lineChartLaporan != null) {
            lineChartLaporan.getData().clear();

            XYChart.Series<String, Number> labaBersih = new XYChart.Series<>();
            labaBersih.setName("Laba Bersih");
            labaBersih.getData().add(new XYChart.Data<>("Feb", 6.5));
            labaBersih.getData().add(new XYChart.Data<>("Mar", 7.5));
            labaBersih.getData().add(new XYChart.Data<>("Apr", 6.5));
            labaBersih.getData().add(new XYChart.Data<>("Mei", 9.5));
            labaBersih.getData().add(new XYChart.Data<>("Jun", 8.5));

            XYChart.Series<String, Number> pendapatanTren = new XYChart.Series<>();
            pendapatanTren.setName("Pendapatan");
            pendapatanTren.getData().add(new XYChart.Data<>("Feb", 18.5));
            pendapatanTren.getData().add(new XYChart.Data<>("Mar", 22.0));
            pendapatanTren.getData().add(new XYChart.Data<>("Apr", 19.5));
            pendapatanTren.getData().add(new XYChart.Data<>("Mei", 25.0));
            pendapatanTren.getData().add(new XYChart.Data<>("Jun", 23.0));

            lineChartLaporan.getData().addAll(labaBersih, pendapatanTren);
        }
    }
}