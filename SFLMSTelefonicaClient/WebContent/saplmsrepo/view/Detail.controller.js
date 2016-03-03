sap.ui.core.mvc.Controller
		.extend(
				"LMS_ODATA3.view.Detail",
				{

					_reportType : null,
					_selectOptions : {},
					_overviewURL : null,
					onInit : function() {
						this.oInitialLoadFinishedDeferred = jQuery.Deferred();

						this.router = sap.ui.core.UIComponent
								.getRouterFor(this);

						if (sap.ui.Device.system.phone) {
							// Do not wait for the master when in mobile phone
							// resolution
							this.oInitialLoadFinishedDeferred.resolve();
						} else {
							// this.getView().setBusy(true);
							var oEventBus = this.getEventBus();
							// oEventBus.subscribe("Component",
							// "MetadataFailed", this.onMetadataFailed, this);
							oEventBus.subscribe("Master",
									"InitialLoadFinished", this.onMasterLoaded,
									this);
						}

						this
								.getRouter()
								.getRoute("detail")
								.attachPatternMatched(this.onRouteMatched, this);

						// read the service url from component config
						this._overviewURL = this.getOwnerComponent()
								.getMetadata().getConfig().serviceConfig.overview;
					},

					onMasterLoaded : function(sChannel, sEvent) {
						this.getView().setBusy(false);
						this.oInitialLoadFinishedDeferred.resolve();
					},

					onMetadataFailed : function() {
						this.getView().setBusy(false);
						this.oInitialLoadFinishedDeferred.resolve();
						this.showEmptyView();
					},

					onRouteMatched : function(oEvent) {
						var currentReportType = oEvent.getParameter("arguments").reportType;
						var genReport = oEvent.getParameter("arguments").genreport;
						
						if (!currentReportType) {
							return;
						}
						if (genReport === "true"){
							this.getView().byId("i_legalEntity").setValue("");
							this.getView().byId("i_itemId").setValue("");
							this.getView().byId("i_offerId").setValue("");
							this.getView().byId("i_numOfDays").setValue("");
							this.onSearch();
						}
						// refresh the page , only report type is changed
						if (this._reportType === currentReportType) {
							return;
						}

						this._reportType = currentReportType;
						this._selectOptions = {
							legalEntity : "",
							itemId : "",
							offerId : "",
							employeeId : "",
							dateSelection : "",
							itemIdVisisble : false,
							numOfDaysVisible : false,
							numOfDays : "",
							offerIdVisible : false,
						};
						if (this._reportType === "Items") {
							this._selectOptions.itemIdVisisble = true;
						} else if (this._reportType === "New Offerings") {
							this._selectOptions.offerIdVisible = true;
							this._selectOptions.numOfDaysVisible = true;
						} else if (this._reportType === "Completed Offerings") {
							this._selectOptions.offerIdVisible = true;
						}
						var oJsonModel = new sap.ui.model.json.JSONModel();
						oJsonModel.setData(this._selectOptions);
						this.getView().byId("selOptionForm").setModel(
								oJsonModel);

						// clear the overview table
						this.getView().byId("overviewTable").unbindRows();

						// clear the generate result
						this.getView().byId("tablecomponent").destroyContent();
					},

					onSearch : function(oEvent) {
						var opt = this.getView().byId("selOptionForm")
								.getModel().getData();
						var legalEntity = opt.legalEntity;
						var itemId = opt.itemId;
						var dateSel = opt.dateSelection;
						if (dateSel) {
							var from = dateSel.split("-")[0];
							/*.replace(
									/[(^\s+)(\s+$)]/g, "");*/
							var to = dateSel.split("-")[1];
							/*.replace(
									/[(^\s+)(\s+$)]/g, "");*/
							var df1 = sap.ui.core.format.DateFormat
									.getDateInstance();
							var df2 = sap.ui.core.format.DateFormat
									.getDateInstance({
										pattern : "dd-MM-yyyy",
										style : "medium"
									});
							dateSel = df2.format(df1.parse(from)) + "-"
									+ df2.format(df1.parse(to));
						}
						var empId = opt.employeeId;
						var offerId = opt.offerId;
						var numOfDays = opt.numOfDays;

						var queryURL = null;
						if (this._reportType === "Items") {
							queryURL = this.getOwnerComponent()
									.setURLParameters(this._overviewURL.item,
											[ legalEntity, itemId, dateSel ]);
						} else if (this._reportType === "New Offerings") {
							queryURL = this.getOwnerComponent()
									.setURLParameters(
											this._overviewURL.newOffer,
											[ legalEntity, offerId, numOfDays,
													dateSel ]);
						} else if (this._reportType === "Completed Offerings") {
							queryURL = this.getOwnerComponent()
									.setURLParameters(
											this._overviewURL.compOffer,
											[ legalEntity, offerId, dateSel ]);
						} else if (this._reportType === "Participants") {
							queryURL = this.getOwnerComponent()
									.setURLParameters(
											this._overviewURL.participant,
											[ legalEntity, empId, dateSel ]);
						}

						var ovTable = this.getView().byId("overviewTable");
						ovTable.setBusy(true);
						var ojsonModel = new sap.ui.model.json.JSONModel(
								queryURL);
						ojsonModel.attachRequestCompleted(function() {
							ovTable.setBusy(false);
						});
						ovTable.setModel(ojsonModel);
						this.getView().byId("overviewTable").bindRows({
							path : "/"
						});
					},

					showEmptyView : function() {
						this.getRouter().myNavToWithoutHash({
							currentView : this.getView(),
							targetViewName : "LMS_ODATA3.view.NotFound",
							targetViewType : "XML"
						});
					},

					fireDetailNotFound : function() {
						this.getEventBus().publish("Detail", "NotFound");
					},

					onNavBack : function() {
						// This is only relevant when running on phone devices
						this.getRouter().myNavBack("main");
					},

					onDetailSelect : function(oEvent) {
						sap.ui.core.UIComponent.getRouterFor(this).navTo(
								"detail",
								{
									entity : oEvent.getSource()
											.getBindingContext().getPath()
											.slice(1),
									tab : oEvent.getParameter("selectedKey")
								}, true);
					},

					openActionSheet : function() {

						if (!this._oActionSheet) {
							this._oActionSheet = new sap.m.ActionSheet(
									{
										buttons : new sap.ushell.ui.footerbar.AddBookmarkButton()
									});
							this._oActionSheet.setShowCancelButton(true);
							this._oActionSheet
									.setPlacement(sap.m.PlacementType.Top);
						}

						this._oActionSheet.openBy(this.getView().byId(
								"actionButton"));
					},

					getEventBus : function() {
						return sap.ui.getCore().getEventBus();
					},

					getRouter : function() {
						return sap.ui.core.UIComponent.getRouterFor(this);
					},

					onExit : function(oEvent) {
						var oEventBus = this.getEventBus();
						oEventBus.unsubscribe("Master", "InitialLoadFinished",
								this.onMasterLoaded, this);
						oEventBus.unsubscribe("Component", "MetadataFailed",
								this.onMetadataFailed, this);
						if (this._oActionSheet) {
							this._oActionSheet.destroy();
							this._oActionSheet = null;
						}
					},

					overviewSelectionChange : function(oEvent) {

						var overviewTable = this.getView()
								.byId("overviewTable");
						var selectedIndex = overviewTable.getSelectedIndex();
						var selectedPath = overviewTable.getRows()[selectedIndex]
								.getBindingContext().sPath;
						var rowNo = selectedPath.match("/\\d+$")[0].replace(
								"/", "");
						var legalEntity = overviewTable.getModel().getData()[rowNo].legalEntity;
						var opt = this.getView().byId("selOptionForm")
								.getModel().getData();

						if (this._reportType === "Items") {
							this.getRouter().navTo("items", {
								LegalEntity : legalEntity,
								query : {
									itemId : opt.itemId,
									dateSel : opt.dateSelection
								}
							});
						} else if (this._reportType === "New Offerings") {
							this.getRouter().navTo("offeringnew", {
								LegalEntity : legalEntity,
								query : {
									offerId : opt.offerId,
									dateSel : opt.dateSelection
								}
							});
						} else if (this._reportType === "Completed Offerings") {
							this.getRouter().navTo("offeringcomp", {
								LegalEntity : legalEntity,
								query : {
									offerId : opt.offerId,
									dateSel : opt.dateSelection
								}
							});
						} else if (this._reportType === "Participants") {
							this.getRouter().navTo("participants", {
								LegalEntity : legalEntity,
								query : {
									empId : opt.empId,
									dateSel : opt.dateSelection
								}
							});
						}
					},

					onGenerateReport : function(oController) {

						// get overview table selection
						var overviewTable = this.getView()
								.byId("overviewTable");
						var selectedIndices = overviewTable
								.getSelectedIndices();
						if (selectedIndices.length === 0) {
							// sap.m.MessageToast.show("Please select at least
							// one Legal Entity");
							// return;
						}
						var legalEntityList = [];
						for (var i = 0; i < selectedIndices.length; i++) {
							var selectedPath = overviewTable.getRows()[selectedIndices[i]]
									.getBindingContext().sPath;
							var legalEntity = this.getView().getModel()
									.getData(selectedPath).LegalEntity;
							legalEntityList.push(legalEntity);
						}

						var options = this.getView().byId("selOptionForm")
								.getModel().getData();
						// TBD please use this criteria to generate the reports.

						var tabledata = 'tablecomponent';
						// var paneltab = 'tablecomponent_panel';
						// var paneldata = this.byId(paneltab);
						// paneldata.destroyLayoutData();

						var vtableData = this.byId(tabledata);
						vtableData.destroyContent();
						vtableData.addContent(new sap.m.Label({
							text : "Report Information"
						}).addStyleClass("tableHeader"));

						var self = this;
						// this.oDataModel = new sap.ui.model.json.JSONModel();
						// this.oDataModel.loadData("../model/RunIdInfoSet.json",
						// {}, false);

						var oTable = this.byId("idRandomDataTable1");
						if (oTable) {
							oTable.destroyContent();
						}
						var oTable = new sap.m.Table("idRandomDataTable1", {
							visibleRowCount : 5,
							columns : [ new sap.m.Column({
								width : "10em",
								header : new sap.m.Label({
									id : "RunidLb",
									text : "{i18n>RunIdInfoID}"
								})
							}), new sap.m.Column({
								width : "20em",
								header : new sap.m.Label({
									id : "datelb",
									text : "{i18n>DatetimeStmp}"
								})
							}), new sap.m.Column({
								width : "15em",
								header : new sap.m.Label({
									id : "xmltypelb",
									text : "{i18n>XMLType}"
								})
							}) ]
						});

						// var oContext = this.getBindingContext();
						// var path = oContext.sPath + "/RunIdInfo";
						var template = new sap.m.ColumnListItem({
							id : "first_template",
							type : "Navigation",

							cells : [ new sap.m.Label({
								// id: "runid",
								text : "{RunId}"
							}), new sap.m.Label({
								// id: "date",
								text : "{DatetimeStmp}"
							}), new sap.m.Label({
								// id: "keyid",
								text : "{XMLType}"
							}) ]
						// press: function(){
						// this.getOwnerComponent().getRouter().navTo("items");
						// }
						});

						template.attachPress(function(oEvent) {
							var context = oEvent.getSource()
									.getBindingContext();
							var runid = context.getProperty("RunId");
							var xmltype = context.getProperty("XMLType");

							if (xmltype === "Items") {
								self.router.navTo("items", {
									runId : runid
								});
							} else if (xmltype === "New Offerings") {
								self.router.navTo("offeringnew", {
									runId : runid
								});
							} else if (xmltype === "Completed Offering") {
								self.router.navTo("offeringcomp", {
									runId : runid
								});
							} else if (xmltype === "Participants") {
								self.router.navTo("participants", {
									runId : runid
								});
							}

						});

						var oFilter = new sap.ui.model.Filter("XMLType",
								sap.ui.model.FilterOperator.EQ,
								this._reportType);
						oTable.bindItems({
							path : "/RunIdInfoSet",
							template : template,
							filters : [ oFilter ]
						});
						vtableData.addContent(oTable);
					},

					onDisplayRecord : function(oEvent) {

						// get overview table selection
						var overviewTable = this.getView()
								.byId("overviewTable");
						var selectedIndex = overviewTable.getSelectedIndex();
						var selectedPath = overviewTable.getRows()[selectedIndex]
								.getBindingContext().sPath;
						var rowNo = selectedPath.match("/\\d+$")[0].replace(
								"/", "");
						var legalEntity = overviewTable.getModel().getData()[rowNo].legalEntity;
						var opt = this.getView().byId("selOptionForm")
								.getModel().getData();
//
						var dateSel = opt.dateSelection;
                        if (dateSel) {
                              var from = dateSel.split("-")[0];
                              /*.replace(
                                          /[(^\s+)(\s+$)]/g, "");*/
                              var to = dateSel.split("-")[1];
                              /*.replace(
                                          /[(^\s+)(\s+$)]/g, "");*/
                              var df1 = sap.ui.core.format.DateFormat
                                          .getDateInstance();
                              var df2 = sap.ui.core.format.DateFormat
                                          .getDateInstance({
                                                pattern : "dd-MM-yyyy",
                                                style : "medium"
                                          });
                              dateSel = df2.format(df1.parse(from)) + "-"
                                          + df2.format(df1.parse(to));
                        }
				
						
						
//					
						if (legalEntity) {
							var LE = legalEntity;
						} else {
							LE = "none";
						}

						if (opt.itemId) {
							var itemId = opt.itemId;
						} else {
							itemId = "none";
						}

						if (opt.offerId) {
							var offerId = opt.offerId;
						} else {
							offerId = "none";
						}

						if (opt.empId) {
							var empId = opt.empId;
						} else {
							empId = "none";
						}

						if (opt.dateSelection) {
							var dateSelection = dateSel;
						} else {
							dateSelection = "none";
						}

						if (this._reportType === "Items") {
							this.getRouter().navTo("items", {
								legalEntity : LE,
								query : {
									itemId : itemId,
									dateSel : dateSelection,
									runId : "none"
								}
							});
						} else if (this._reportType === "New Offerings") {
							this.getRouter().navTo("offeringnew", {
								legalEntity : LE,
								query : {
									offerId : offerId,
									dateSel : dateSelection,
									runId : "none"
								}
							});
						} else if (this._reportType === "Completed Offerings") {
							this.getRouter().navTo("offeringcomp", {
								legalEntity : LE,
								query : {
									offerId : offerId,
									dateSel : dateSelection,
									runId : "none"
								}
							});
						} else if (this._reportType === "Participants") {
							this.getRouter().navTo("participants", {
								legalEntity : LE,
								query : {
									empId : empId,
									dateSel : dateSelection,
									runId : "none"
								}
							});
						}

						// if(selectedIndices.length === 0){
						// // sap.m.MessageToast.show("Please select at least
						// one Legal Entity");
						// // return;
						// }
						// var legalEntityList = [];
						// for(var i = 0 ; i < selectedIndices.length; i++){
						// var selectedPath =
						// overviewTable.getRows()[selectedIndices[i]].getBindingContext().sPath;
						// var legalEntity =
						// this.getView().getModel().getData(selectedPath).LegalEntity;
						// legalEntityList.push(legalEntity);
						// }

						// var options =
						// this.getView().byId("selOptionForm").getModel().getData();
						// if (this._reportType == "Items") {
						// self.router.navTo("items", context);
						// } else if (this._reportType == "New Offering") {
						// self.router.navTo("offeringnew", context);
						// } else if (this._reportType == "Completed Offering")
						// {
						// self.router.navTo("offeringcomp", context);
						// } else if (this._reportType == "Participants") {
						// self.router.navTo("participants", context);
						// }

						// //get overview table selection
						// var overviewTable =
						// this.getView().byId("overviewTable");
						// var selectedIndices =
						// overviewTable.getSelectedIndices();
						// if(selectedIndices.length === 0){
						// sap.m.MessageToast.show("Please select at least one
						// Legal Entity");
						// return;
						// }
						// var legalEntityList = [];
						// for(var i = 0 ; i < selectedIndices.length; i++){
						// var selectedPath =
						// overviewTable.getRows()[selectedIndices[i]].getBindingContext().sPath;
						// var legalEntity =
						// this.getView().getModel().getData(selectedPath).LegalEntity;
						// legalEntityList.push(legalEntity);
						// }

						// var options =
						// this.getView().byId("selOptionForm").getModel().getData();
						// // TBD please use this criteria to generate the
						// reports.

						// var tabledata = 'tablecomponent';
						// // var paneltab = 'tablecomponent_panel';
						// // var paneldata = this.byId(paneltab);
						// // paneldata.destroyLayoutData();

						// var vtableData = this.byId(tabledata);
						// vtableData.destroyContent();
						// vtableData.addContent(new sap.m.Label({
						// text: "Report Information"
						// }).addStyleClass("tableHeader"));

						// var self = this;
						// // this.oDataModel = new
						// sap.ui.model.json.JSONModel();
						// //
						// this.oDataModel.loadData("../model/RunIdInfoSet.json",
						// {}, false);

						// var oTable = this.byId("idRandomDataTable1");
						// if (oTable) {
						// oTable.destroyContent();
						// }
						// var oTable = new sap.m.Table("idRandomDataTable1", {
						// visibleRowCount: 5,
						// columns: [new sap.m.Column({
						// width: "10em",
						// header: new sap.m.Label({
						// id: "RunidLb",
						// text: "{i18n>XMLType}"
						// })
						// }), new sap.m.Column({
						// width: "20em",
						// header: new sap.m.Label({
						// id: "datelb",
						// text: "{i18n>DatetimeStmp}"
						// })
						// }),
						// new sap.m.Column({
						// width: "15em",
						// header: new sap.m.Label({
						// id: "xmltypelb",
						// text: "{i18n>LegalEntity}"
						// })
						// })
						// ]
						// });

						// // var oContext = this.getBindingContext();
						// // var path = oContext.sPath + "/RunIdInfo";
						// var template = new sap.m.ColumnListItem({
						// id: "first_template",
						// type: "Navigation",

						// cells: [new sap.m.Label({
						// //id: "runid",
						// text: "{XMLType}"
						// }),
						// new sap.m.Label({
						// // id: "date",
						// text: "{DatetimeStmp}"
						// }),
						// new sap.m.Label({
						// // id: "keyid",
						// text: "{LegalEntity}"
						// })]
						// //press: function(){
						// //
						// this.getOwnerComponent().getRouter().navTo("items");
						// //}
						// });

						// template.attachPress(function(oEvent) {
						// var context = oEvent.getSource().getBindingContext();
						// var runid = context.getProperty("RunId");
						// var xmltype = context.getProperty("XMLType");

						// if (xmltype == "Items") {
						// self.router.navTo("items", context);
						// } else if (xmltype == "New Offering") {
						// self.router.navTo("offeringnew", context);
						// } else if (xmltype == "Completed Offering") {
						// self.router.navTo("offeringcomp", context);
						// } else if (xmltype == "Participants") {
						// self.router.navTo("participants", context);
						// }

						// });

						// var oFilter = new sap.ui.model.Filter("XMLType",
						// sap.ui.model.FilterOperator.EQ, this._reportType);
						// oTable.bindItems({path:"/RunIdInfoSet", template:
						// template, filters:[oFilter]});
						// vtableData.addContent(oTable);
					},

					pressListMethod : function() {
						// var oSelectedKey =
						// sap.ui.getCore().byId("keyid").getSelectedItem().getKey();
						this.getOwnerComponent().getRouter().navTo("items");
					}
				});