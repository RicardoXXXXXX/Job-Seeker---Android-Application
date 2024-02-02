package com.example.jobseeker;

import java.util.ArrayList;
import java.util.List;

public class JobModel {
    private List<Job> jobListings;

    public List<Job> getJobListings() {
        return jobListings;
    }

    public class Job {
        private int id;
        private String jobTitle;
        private String category;
        private String companyName;
        private String description;
        private List<String> requirements;
        private String responsibility;
        private String salaryRange;
        private List<String> benefits;
        private String postingDate;
        private String workType;
        private List<String> location;

        // Constructor
        public Job() {
            requirements = new ArrayList<>();
            benefits = new ArrayList<>();
            location = new ArrayList<>();
        }

        // Getter and setter methods for all properties
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getRequirements() {
            return requirements;
        }

        public void setRequirements(List<String> requirements) {
            this.requirements = requirements;
        }

        public String getResponsibility() {
            return responsibility;
        }

        public void setResponsibility(String responsibility) {
            this.responsibility = responsibility;
        }

        public String getSalaryRange() {
            return salaryRange;
        }

        public void setSalaryRange(String salaryRange) {
            this.salaryRange = salaryRange;
        }

        public List<String> getBenefits() {
            return benefits;
        }

        public void setBenefits(List<String> benefits) {
            this.benefits = benefits;
        }

        public String getPostingDate() {
            return postingDate;
        }

        public void setPostingDate(String postingDate) {
            this.postingDate = postingDate;
        }

        public String getWorkType() {
            return workType;
        }

        public void setWorkType(String workType) {
            this.workType = workType;
        }

        public List<String> getLocation() {
            return location;
        }

        public void setLocation(List<String> location) {
            this.location = location;
        }
    }
}
