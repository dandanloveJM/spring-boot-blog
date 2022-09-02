package hello.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * type值：
 *  1:采集设计
 * 2: 科研项目,
 * 3: 现场处理,
 * 4: 质量评价,
 * 5: 资料分析,
 * 6: 表层调查
 * 7: 测量质控
 * 8: 技术支持,
 * 9: 现场支持
 * 10: 党建工作
 * */


public class Project {
    private Integer id;
    private String processId;
    private String name;
    private String number;
    private Integer type;
    private String attachment;
    private Integer ownerId;
    private Integer productId;
    private Instant createdAt;
    private Instant updatedAt;
    private BigDecimal totalProduct;
    private BigDecimal totalPercentage;
    private String taskId;
    private List<Product> products;
    private String ownerName;
    private String activityName;
    private Integer pmId;
    private Boolean isNew;
    private Boolean isStep2New;
    private Boolean isStep3New;
    private Boolean isStep4New;
    private Boolean isStep5New;


    public Project(){}


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(BigDecimal totalProduct) {
        this.totalProduct = totalProduct;
    }

    public BigDecimal getTotalPercentage() {
        return totalPercentage;
    }

    public void setTotalPercentage(BigDecimal totalPercentage) {
        this.totalPercentage = totalPercentage;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getPmId() {
        return pmId;
    }

    public void setPmId(Integer pmId) {
        this.pmId = pmId;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean aNew) {
        isNew = aNew;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public Boolean getStep2New() {
        return isStep2New;
    }

    public void setStep2New(Boolean step2New) {
        isStep2New = step2New;
    }

    public Boolean getStep3New() {
        return isStep3New;
    }

    public void setStep3New(Boolean step3New) {
        isStep3New = step3New;
    }

    public Boolean getStep4New() {
        return isStep4New;
    }

    public void setStep4New(Boolean step4New) {
        isStep4New = step4New;
    }

    public Boolean getStep5New() {
        return isStep5New;
    }

    public void setStep5New(Boolean step5New) {
        isStep5New = step5New;
    }
}
