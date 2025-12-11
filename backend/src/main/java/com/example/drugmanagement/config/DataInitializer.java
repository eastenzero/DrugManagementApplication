package com.example.drugmanagement.config;

import com.example.drugmanagement.entity.DrugCategory;
import com.example.drugmanagement.entity.DrugInfo;
import com.example.drugmanagement.entity.StockIn;
import com.example.drugmanagement.entity.StockOut;
import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.DrugCategoryRepository;
import com.example.drugmanagement.repository.DrugInfoRepository;
import com.example.drugmanagement.repository.StockInRepository;
import com.example.drugmanagement.repository.StockOutRepository;
import com.example.drugmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DrugCategoryRepository categoryRepository;
    private final DrugInfoRepository drugInfoRepository;
    private final StockInRepository stockInRepository;
    private final StockOutRepository stockOutRepository;
    private final UserRepository userRepository;

    public DataInitializer(DrugCategoryRepository categoryRepository,
                           DrugInfoRepository drugInfoRepository,
                           StockInRepository stockInRepository,
                           StockOutRepository stockOutRepository,
                           UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.drugInfoRepository = drugInfoRepository;
        this.stockInRepository = stockInRepository;
        this.stockOutRepository = stockOutRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            DrugCategory c1 = new DrugCategory();
            c1.setName("抗生素");
            c1.setDescription("常用抗生素类药品");
            c1.setCreateTime(LocalDateTime.now());

            DrugCategory c2 = new DrugCategory();
            c2.setName("解热镇痛");
            c2.setDescription("退烧止痛类药品");
            c2.setCreateTime(LocalDateTime.now());

            DrugCategory c3 = new DrugCategory();
            c3.setName("维生素");
            c3.setDescription("维生素及营养补充剂");
            c3.setCreateTime(LocalDateTime.now());

            categoryRepository.saveAll(Arrays.asList(c1, c2, c3));
        }

        if (drugInfoRepository.count() == 0) {
            List<DrugCategory> categories = categoryRepository.findAll();
            if (!categories.isEmpty()) {
                DrugCategory c1 = categories.get(0);
                DrugCategory c2 = categories.size() > 1 ? categories.get(1) : c1;

                DrugInfo d1 = new DrugInfo();
                d1.setName("阿莫西林胶囊");
                d1.setCategory(c1);
                d1.setSpecification("0.25g*24粒");
                d1.setUnit("盒");
                d1.setPrice(new BigDecimal("18.50"));
                d1.setStock(120);
                d1.setManufacturer("某某制药股份有限公司");
                d1.setProductionDate(LocalDate.now().minusMonths(2));
                d1.setExpireDate(LocalDate.now().plusYears(2));
                d1.setStatus(1);
                d1.setCreateTime(LocalDateTime.now());

                DrugInfo d2 = new DrugInfo();
                d2.setName("布洛芬片");
                d2.setCategory(c2);
                d2.setSpecification("0.2g*20片");
                d2.setUnit("盒");
                d2.setPrice(new BigDecimal("15.00"));
                d2.setStock(80);
                d2.setManufacturer("XX 医药有限公司");
                d2.setProductionDate(LocalDate.now().minusMonths(1));
                d2.setExpireDate(LocalDate.now().plusYears(1));
                d2.setStatus(1);
                d2.setCreateTime(LocalDateTime.now());

                DrugInfo d3 = new DrugInfo();
                d3.setName("维生素 C 片");
                d3.setCategory(c2);
                d3.setSpecification("100mg*100片");
                d3.setUnit("瓶");
                d3.setPrice(new BigDecimal("22.00"));
                d3.setStock(45);
                d3.setManufacturer("营养制药厂");
                d3.setProductionDate(LocalDate.now().minusMonths(3));
                d3.setExpireDate(LocalDate.now().plusYears(1));
                d3.setStatus(1);
                d3.setCreateTime(LocalDateTime.now());

                drugInfoRepository.saveAll(Arrays.asList(d1, d2, d3));
            }
        }

        if (stockInRepository.count() == 0 || stockOutRepository.count() == 0) {
            List<DrugInfo> drugs = drugInfoRepository.findAll();
            if (!drugs.isEmpty()) {
                DrugInfo d1 = drugs.get(0);
                DrugInfo d2 = drugs.size() > 1 ? drugs.get(1) : d1;
                User operator = null;
                List<User> users = userRepository.findAll();
                if (!users.isEmpty()) {
                    operator = users.get(0);
                }

                if (stockInRepository.count() == 0) {
                    StockIn s1 = new StockIn();
                    s1.setDrug(d1);
                    s1.setQuantity(50);
                    s1.setPrice(new BigDecimal("17.00"));
                    s1.setSupplier("华东医药仓库");
                    s1.setBatchNo("IN-" + System.currentTimeMillis());
                    s1.setInTime(LocalDateTime.now().minusDays(5));
                    s1.setOperator(operator);
                    s1.setRemark("首批进货");

                    StockIn s2 = new StockIn();
                    s2.setDrug(d2);
                    s2.setQuantity(30);
                    s2.setPrice(new BigDecimal("14.00"));
                    s2.setSupplier("华南医药仓库");
                    s2.setBatchNo("IN-" + (System.currentTimeMillis() - 100000));
                    s2.setInTime(LocalDateTime.now().minusDays(3));
                    s2.setOperator(operator);
                    s2.setRemark("补货");

                    stockInRepository.saveAll(Arrays.asList(s1, s2));
                }

                if (stockOutRepository.count() == 0) {
                    StockOut o1 = new StockOut();
                    o1.setDrug(d1);
                    o1.setQuantity(10);
                    o1.setReason("门诊使用");
                    o1.setOutTime(LocalDateTime.now().minusDays(2));
                    o1.setOperator(operator);
                    o1.setRemark("日常发药");

                    StockOut o2 = new StockOut();
                    o2.setDrug(d2);
                    o2.setQuantity(5);
                    o2.setReason("报废处理");
                    o2.setOutTime(LocalDateTime.now().minusDays(1));
                    o2.setOperator(operator);
                    o2.setRemark("临近有效期");

                    stockOutRepository.saveAll(Arrays.asList(o1, o2));
                }
            }
        }
    }
}
