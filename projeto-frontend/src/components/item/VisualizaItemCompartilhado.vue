<template>
    <div>
        <div class="row">
            <div class="col-sm-4 text-left">
                <div class="panel panel-primary">
                    <h2 class="panel-heading">
                        Visualizar Item
                    </h2>
                    <ul class="list-group">
                        <li class="list-group-item">
                            <h3>Nome</h3>
                            <p>{{item.nome}}</p>
                        </li>
                        <li class="list-group-item">
                            <h3>Descrição</h3>
                            <p>{{item.descricao}}</p>
                        </li>
                        <li class="list-group-item">
                            <h3>Tipo</h3>
                            <p>{{item.tipo}}</p>
                        </li>
                    </ul>
                    <div class="panel-footer">
                        <button data-toggle="modal" data-target="#modalCompartilhar" class="btn btn-primary">Compartilhar</button>
                    </div>
                </div>
            </div>
            <div class="col-sm-8 text-center">
                <div class="panel panel-primary">
                    <h2 class="panel-heading">
                        Próximos Compartilhamentos
                    </h2>
                    <div style="padding: 0px 10px">
                        <table v-if="compartilhamentos.length > 0" class="table table-striped table-hover" id="tbCompartilhamentos">
                            <thead>
                            <tr>
                                <th>Início</th>
                                <th>Término</th>
                                <th>Usuário</th>
                                <th>Status</th>
                                <th class="commands"></th>
                            </tr>
                            </thead>

                            <tbody>
                            <tr v-for="compartilhamento in compartilhamentos">
                                <td>{{compartilhamento.data_inicio}}</td>
                                <td>{{compartilhamento.data_termino}}</td>
                                <td>{{compartilhamento.usuario_nome}}</td>
                                <td>{{compartilhamento.status}}</td>
                                <td>
                                    <a @click="compartilhamentoSelecionado=compartilhamento.id"  data-toggle="modal" data-target="#modalCancelar" class="glyphicon glyphicon-remove link"></a>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <h3 style="margin-bottom: 5px" v-if="compartilhamentos.length == 0">Não há nenhum compartilhamento futuro aceito ou aberto.</h3>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-4">

            </div>
            <div class="col-sm-8">

            </div>
        </div>


        <!-- MODAL DE COMPARTILHAMENTO -->
        <div id="modalCompartilhar" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form @submit.prevent="processForm">
                    <div class="modal-header">
                        <h2 class="modal-title">Compartilhar item</h2>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                            <div class="form-group">
                                <label for="email">Email do usuário</label>
                                <input v-model="compartilhamento.email" type="email" class="form-control" id="email" aria-describedby="emailHelp" placeholder="Email do usuário que receberá o item">
                            </div>
                            <div class="row">
                                <div class="col-sm-6">
                                    <div class="form-group">
                                        <label for="dateInicio">Início</label>
                                        <input v-model="compartilhamento.dataInicio" type="date" class="form-control" id="dateInicio" aria-describedby="dateInicioHelp" >
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="form-group">
                                        <label for="dateTermino">Término</label>
                                        <input v-model="compartilhamento.dataTermino" type="date" class="form-control" id="dateTermino" aria-describedby="dateTerminoHelp" >
                                    </div>
                                </div>
                            </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">Compartilhar</button>
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <div v-if="success" class="alert alert-success" role="alert">
                            Compartilhamento registrado com sucesso!
                            <button type="button" @click="success = false" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div v-if="errors" class="alert alert-danger" role="alert">
                            Erro ao registrar compartilhamento: {{errors.message}}
                            <button type="button" @click="errors = null" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </div>
                    </form>

                </div>
            </div>
        </div>

        <!-- MODAL DE CANCELAMENTO -->
        <div id="modalCancelar" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title">Cancelar Compartilhamento</h2>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <p>Tem certeza que deseja cancelar o compartilhamento?</p>
                    </div>
                    <div class="modal-footer">
                        <button @click="cancelarCompartilhamento" class="btn btn-danger">Sim</button>
                        <button class="btn btn-primary" data-dismiss="modal">Não</button>
                        <div v-if="success" class="alert alert-success" role="alert">
                            Compartilhamento cancelado com sucesso!
                            <button type="button" @click="success = false" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div v-if="errors" class="alert alert-danger" role="alert">
                            Erro ao cancelar compartilhamento: {{errors.message}}
                            <button type="button" @click="errors = null" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    export default {

        props: ['item'],

        created() {
            this.updateList()
        },

        name: "VisualizaItemCompartilhado",
        data() {
            return {
                compartilhamento: {
                    email: "",
                    dataInicio: "",
                    dataTermino: "",
                    itemId: this.item.id
                },
                compartilhamentoSelecionado: null,
                success: false,
                errors: null,
                compartilhamentos: [],

                httpOptions: {
                    baseURL: this.$root.config.url,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + this.$root.credentials.token
                    }
                }
            }
        },
        methods: {
            processForm: function() {
                axios.put("/api/compartilhamento/novo", this.compartilhamento, this.httpOptions)
                    .then(response => {
                        this.success = true;
                        this.errors = null;
                        this.error = {};
                        console.log(response)
                        this.updateList();
                    })
                    .catch(error => {
                        this.errors = error.response.data.errors;
                        this.success = false;
                        console.log(error);
                    });
            },
            updateList() {
                axios.get(`/api/compartilhamento/lista?itemId=${this.item.id}`, this.httpOptions)
                    .then(result => {
                        this.compartilhamentos = result.data.data.filter(compartilhamento => {
                            let d1 = Date.parse(compartilhamento.data_termino);
                            let d2 = Date.now();
                            console.log(compartilhamento.usuario);
                            return d1 > d2 && (compartilhamento.status == "Aberto" || compartilhamento.status == "Aceito") ;
                        })
                    })
                    .catch(err => {
                        console.log(err);
                    });
            },
            cancelarCompartilhamento(){
                axios.delete(`/api/compartilhamento/delete/${this.compartilhamentoSelecionado}`, this.httpOptions)
                .then(response => {
                    this.success = true;
                    this.errors = null;
                    this.updateList();
                })
                .catch(err => {
                    this.errors = err.response.data.errors;
                    this.success = false;
                })
            }
        }
    }
</script>

<style scoped>
    th.commands {
        width: 24px;
    }
    .row {
        margin-top: 25px;
    }
    th {
        text-align: center;
    }
    td {
        text-align: center;
    }
    .alert {
        margin-top: 10px;
        margin-bottom: 0px !important;
    }
    .modal h2 {
        display: inline-block;
    }
    .modal-footer .alert {
        text-align: left;
    }

</style>