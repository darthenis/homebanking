const { createApp } = Vue;

createApp({
  data() {
    return {
      clientsData: [],
      newClient: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        roleType : ""
      },
      errors: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        roleType: ""
      },
      newLoan:{
        name: "",
        maxAmount: "",
        interest: "",
        payments : [],
        prePayment : ""
      },
      loanErrors:{
        name: "",
        maxAmount: "",
        interest: "",
        payments: ""
      },
      editErrors: {
        firstName: "",
        lastName: "",
        email: "",
      },
      selectedClient: null,
      selectedErrors: {
        firstName: "",
        lastName: "",
        email: "",
      },
      isLoading: {
        create : false,
        edit : false,
        delete: false
      }
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      axios
        .get("http://localhost:8080/api/clients")
        .then((res) => {
          this.clientsData = res.data;
        })
        .catch((err) => console.log(err));
    },
    resetNewClient() {
      this.newClient = { firstName: "", lastName: "", email: "" };
    },
    createClient() {

      this.validateInputs(true, this.errors, this.newClient);

      if (!Object.values(this.errors).some((e) => e !== "")) {
        this.setLoading(`create`);
        axios
          .post("/api/clients", `firstName=${this.newClient.firstName}&lastName=${this.newClient.lastName}&email=${this.newClient.email}&password=${this.newClient.password}&roleType=${this.newClient.roleType}`,
          {headers:{'content-type':'application/x-www-form-urlencoded'}})
          .then((res) => {
            this.loadData();
            this.messageAlert("Client created succesfully");
            this.resetNewClient();
            this.setLoading("create");

          })
          .catch((err) => {
            this.setLoading("create");
            console.log(err)
          });
      }
    },
    deleteClient() {
      this.setLoading("delete");
      axios.delete(this.selectedClient._links.self.href)
        .then((res) => {
          this.loadData();
          this.closeModal("closeDeleteModal")
          this.messageAlert("Client deleted succesfully");
          this.selectedClient = null;
          this.setLoading("delete");
      });
    },
    check(){



    },
    addPayment(){

      if(this.newLoan.payments.some(payment => payment == this.newLoan.prePayment)){

        this.loanError.payments= "the payments cant be repeat";

      } else if(this.newLoan.prePayment){

        this.newLoan.payments.push(this.newLoan.prePayment);

        this.newLoan.prePayment = ""
      }

    },
    cleanLoanForm(){

      this.newLoan.name = "";
      this.newLoan.maxAmount = "";
      this.newLoan.payments = "",
      this.newLoan.interest = "";

    },
    createLoan(){

      this.validateInputs(true, this.loanErrors, this.newLoan);

      if (!Object.values(this.loanErrors).some((e) => e !== "")) {

        axios.post( '/api/loans', 
                    `name=${this.newLoan.name}&maxAmount=${this.newLoan.maxAmount}&payments=${this.newLoan.payments}&interest=${this.newLoan.interest}`,
                    {headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(res => {

                      this.loadData();
                      this.messageAlert("Loan created succesfully");
                      console.log(res);
                      this.cleanLoanForm();

                    })
                    .catch(err => console.log(err))

      }



    },
    restPayment(){

      this.newLoan.payments.pop();

    },
    selectClient(client) {
      this.selectedClient = { ...client };
    },
    cancelEdit() {
      this.selectedClient = null;
    },
    putClient() {
      this.validateInputs(true, this.editErrors, this.selectedClient);
      if (!Object.values(this.editErrors).some((e) => e !== "")) {
        this.setLoading("edit");
        axios
          .put(this.selectedClient._links.self.href, this.selectedClient)
          .then((res) => {
            this.setLoading("edit");
            this.loadData();
            this.closeModal("editModalClose")
            this.messageAlert("Client edited succesfully");
            this.selecteClient = null;
          });
      }
    },
    messageAlert(message) {
      Toastify({
        text: message,
        className: "info",
        position: "center",
        duration: 2000,
        style: {
          background: "#b6e4ff",
          color: "black",
          borderRadius: "10px",
        },
      }).showToast();
    },
    validateInputs(isSending, errors, data) {

      for (let [key] of Object.entries(errors)) {
        if (isSending && !data[key]) {
          errors[key] = "*required field";
        } else if (data[key]) {
          errors[key] = "";
        }

        if (data[key]) {
          if (
            !/^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$/g.test(data[key]) &&
            (key === "firstName" || key === "lastName")
          )
            errors[key] = "*just introduce letters";
          else if (
                    key === "email" &&
                    !data[key]
                      .toLowerCase()
                      .match(
                        /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                      )
                  )
            errors[key] = "*introduce an validate email";
          else if (
            (key === "maxAmount" || key === "interest") &&
            !/^\d+$/.test(data[key])
          )
          errors[key] = "*Introduce an validate number";
          else if(errors[key] !== "") errors[key] = ""
        }
      }
    },
    setLoading(key){

      this.isLoading[key] = !this.isLoading[key];

    },
    closeModal(id){

      let button = document.getElementById(id);

      button.click();

    },
    logout(){

      axios('/api/logout')
          .then(res => {

            location.reload();

          })

    }
  },
  computed: {
    validateChange() {
      this.validateInputs(false, this.errors, this.newClient);
      this.validateInputs(false, this.loanErrors, this.newLoan);
    },
    validateChangesEdit() {
      this.validateInputs(false, this.editErrors, this.selectedClient);
    },
  },
}).mount("#app");
