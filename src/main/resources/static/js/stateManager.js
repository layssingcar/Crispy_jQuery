export const stateManager = {
    isVerified: false,
    setIsVerified(value) {
        this.isVerified = value;
    },
    getIsVerified() {
        return this.isVerified;
    }
};

