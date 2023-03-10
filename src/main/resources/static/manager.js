const { createApp } = Vue;

createApp({
  data() {
    return {
      clientsData: null,
      newClient: {
        firstName: "",
        lastName: "",
        email: "",
      },
      errors: {
        firstName: "",
        lastName: "",
        email: "",
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
        .get("http://localhost:8080/clients")
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
        this.setLoading("create");
        axios
          .post("http://localhost:8080/clients", this.newClient)
          .then((res) => {
            this.loadData();
            this.messageAlert("Client created succesfully");
            this.resetNewClient();
            this.setLoading("create");
          })
          .catch((err) => console.log(err));
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
            !/^[A-Za-z???????????????????????? ]+$/g.test(data[key]) &&
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
        }
      }
    },
    setLoading(key){

      this.isLoading[key] = !this.isLoading[key];

    },
    closeModal(id){

      let button = document.getElementById(id);

      button.click();

    }
  },
  computed: {
    validateChange() {
      this.validateInputs(false, this.errors, this.newClient);
    },
    validateChangesEdit() {
      this.validateInputs(false, this.editErrors, this.selectedClient);
    },
  },
}).mount("#app");
