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
        axios
          .post("http://localhost:8080/clients", this.newClient)
          .then((res) => {
            this.loadData();
            this.messageAlert("Client created succesfully");
            this.resetNewClient();
          })
          .catch((err) => console.log(err));
      }
    },
    deleteClient() {
      axios.delete(this.selectedClient._links.self.href).then((res) => {
        this.loadData();
        this.messageAlert("Client deleted succesfully");
        this.selectedClient = null;
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
        axios
          .put(this.selectedClient._links.self.href, this.selectedClient)
          .then(() => {
            this.loadData();
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
        }
      }
    },
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
