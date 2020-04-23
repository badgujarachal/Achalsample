package com.softcell.gonogo.hdfctw.mvp.views.post_sanction.digital_document

import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.softcell.gonogo.BuildConfig
import com.softcell.gonogo.R
import com.softcell.gonogo.firebaseGoogleAnalytics.logEvents
import com.softcell.gonogo.firebaseGoogleAnalytics.GoogleAnalyticsEvents
import com.softcell.gonogo.hdfctw.mvp.views.post_sanction.digital_document.adapter.DoDocumentsAdapter
import com.softcell.soutilities.GngUtility
import com.softcell.soutilities.enums.*
import com.softcell.soutilities.log.gngx.showErrorMessage
import com.softcell.soutilities.mvp.models.do_display.Document
import com.softcell.soutilities.mvp.models.do_display.DocumentWithAssetDetails
import com.softcell.soutilities.mvp.models.post_ipa.DownloadDigitalDocumentRequest
import com.softcell.soutilities.mvp.models.post_ipa.PostIpaRequest
import com.softcell.soutilities.mvp.models.submit_application.Application
import com.softcell.soutilities.mvp.presenters.post_ipa.digital_document.DigitalDocumentContract
import com.softcell.soutilities.mvp.presenters.post_ipa.digital_document.DigitalDocumentPresenter
import com.softcell.soutilities.mvp.presenters.post_ipa.do_downloads.DODownloadContract
import com.softcell.soutilities.mvp.presenters.post_ipa.do_downloads.DODownloadPresenter
import com.softcell.soutilities.mvp.view.fragements.BaseValidationFragment
import com.softcell.soutilities.network.APIClientUtils
import com.softcell.soutilities.recycler_view_decorators.VerticalSpaceItemDecoration
import com.softcell.soutilities.requests_response.ApplicationHeaderUtility
import com.softcell.soutilities.requests_response.ApplicationRequestUtility
import com.softcell.soutilities.storage.SpUtils
import kotlinx.android.synthetic.hdfctw.fragment_digital_documents.*
import org.jetbrains.anko.image


/**
 * Created by Abhishek.s on 27,June,2019
 */

abstract class BaseDigitalDocumentsFragment : BaseValidationFragment(), DODownloadContract.View,
        DigitalDocumentContract.View, View.OnClickListener {

    private val ACH_MANDATE = "ach_mandate"
    private val APPLICATION_FORM = "application_form"
    private val LOAN_AGREEMENT = "loan_agreement"
    private val DO_DOCUMENT_POSTFIX = "_DO"
    private var DOCUMENT_CHECKLIST = "document_checklist"
    private var APPROVAL_LETTER = "loan_approval"
    private var AADHAR_CONSENT_LETTER = "aadhar_consent_letter"
    private var dODownloadPresenter: DODownloadPresenter? = null
    private var digitalDocumentPresenter: DigitalDocumentPresenter? = null
    private var isDoDownloaded = false
    private var isAchDownloaded = false
    private var isApplicationFormDownloaded = false
    private var isDocChecklistDownloaded = false
    private var isApprovalLetterDownloaded = false
    private var isAgreementLetterDownloaded = false
    private var isAddharConsentDownloaded = false
    var fileName: String? = null
    private var doDocumentAdapter: DoDocumentsAdapter? = null
    private var doMap = HashMap<DocumentWithAssetDetails, String>()
    private var listDeliveryDocuments: List<DocumentWithAssetDetails>? = null
    private var dmsCode: String? = null
    private var deliveryOrderId: String? = null
    private var fileId: String? = null
    abstract fun onDisplayDigitalDocuments(docUri: Uri, docTitle: String)
    abstract fun onDisplayInvoiceScreen()
    abstract fun onApprovalLetterCancelClicked(approvalLetterId: String?, fileId: String?)

    override fun getFragmentLayout(): View {
        return inflater.inflate(R.layout.fragment_digital_documents, sv_digital_documents_parent)
    }

    override fun initializeViews() {
        //initialize all views herea
        view_delivery_orders.setOnClickListener(this)
        view_application_form.setOnClickListener(this)
        view_ach_mandate.setOnClickListener(this)
        view_document_form.setOnClickListener(this)
        view_document_loan_approval.setOnClickListener(this)
        view_document_loan_agreement.setOnClickListener(this)
        view_document_aadhar_consent.setOnClickListener(this)
        civ_application_form.setOnClickListener(this)
        civ_ach_mandate.setOnClickListener(this)
        civ_document_form.setOnClickListener(this)
        civ_document_loan_approval.setOnClickListener(this)
        civ_document_loan_agreement.setOnClickListener(this)
        civ_document_aadhar_consent.setOnClickListener(this)

        btn_next.setOnClickListener(this)
        btn_cancel_approval_letter.setOnClickListener(this)

        if (ApplicationRequestUtility.getInstance().ekycResponse != null) {
            view_document_aadhar_consent.visibility = View.VISIBLE
        } else {
            view_document_aadhar_consent.visibility = View.GONE
        }

        initRecycler()

        if (isToShowAch()) {
            view_ach_mandate.visibility = View.VISIBLE
        } else {
            view_ach_mandate.visibility = View.GONE
        }
    }

    private fun isToShowAch(): Boolean {

        return if (ApplicationRequestUtility.getInstance().applicantBankingList != null) {
            !ApplicationRequestUtility.getInstance().applicantBankingList[0]
                    .ifscCode.contains("HDFC", true) ||
                    (ApplicationRequestUtility.getInstance().applicantBankingList[0]
                            .ifscCode.contains("HDFC", true) &&
                            ApplicationRequestUtility.getInstance().applicantBankingList[0]
                                    .subMemberBank.equals("Y", true))
        } else {
            false
        }
    }


    override fun initializePresenter() {
        //initialize all presenters here
        dODownloadPresenter = DODownloadPresenter()
        dODownloadPresenter?.attachView(this)
        dODownloadPresenter?.attachSharedPreference(SpUtils.getInstance(context))

        digitalDocumentPresenter = DigitalDocumentPresenter()
        digitalDocumentPresenter?.attachView(this)
        digitalDocumentPresenter?.attachSharedPreference(SpUtils.getInstance(context))


    }


    override fun setDataIfPresent() {
        //populate data on views here
        if (isDoDownloaded) {
            delivery_order_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
        }

        if (isApplicationFormDownloaded) {
            application_form_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
        }

        if (isAchDownloaded) {
            ach_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
        }

        if (isDocChecklistDownloaded) {
            checklist_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
        }

        if (isApprovalLetterDownloaded) {
            loan_approval_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
        }

        if (isAgreementLetterDownloaded) {
            loan_agreement_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
        }
    }

    override fun isScreenValidData(): Boolean {


        if (view_delivery_orders.visibility == View.VISIBLE && !isDoDownloaded) {
            showErrorMessage(getString(R.string.download_document), context, null)
            return false
        }

        if (view_application_form.visibility == View.VISIBLE && !isApplicationFormDownloaded) {
            showErrorMessage(getString(R.string.download_document), context, null)
            return false
        }

        if (view_ach_mandate.visibility == View.VISIBLE && !isAchDownloaded) {
            showErrorMessage(getString(R.string.download_document), context, null)
            return false
        }

        if (view_document_form.visibility == View.VISIBLE && !isDocChecklistDownloaded) {
            showErrorMessage(getString(R.string.download_document), context, null)
            return false
        }

        if (view_document_loan_approval.visibility == View.VISIBLE && !isApprovalLetterDownloaded) {
            showErrorMessage(getString(R.string.download_document), context, null)
            return false
        }

        if (view_document_loan_agreement.visibility == View.VISIBLE && !isAgreementLetterDownloaded) {
            showErrorMessage(getString(R.string.download_document), context, null)
            return false
        }

        if (view_document_aadhar_consent.visibility == View.VISIBLE && !isAddharConsentDownloaded) {
            showErrorMessage(getString(R.string.download_document), context, null)
            return false
        }

        return true
    }

    override fun getDataFromViews() {
        //fetch data from views here
    }

    override fun onClick(view: View?) {
        //all click listeners here
        when (view) {
            view_delivery_orders -> {

                if (elDeliveryOrder.isExpanded) {
                    elDeliveryOrder.collapse()
                    delivery_order_card_iv.image = context?.resources?.getDrawable(R.drawable.ic_expand_more_black_24dp, null)
                } else {
                    elDeliveryOrder.expand()
                    delivery_order_card_iv.image = context?.resources?.getDrawable(R.drawable.ic_expand_less_black_24dp, null)
                }
            }

            view_application_form -> {

                if (elApplicationForm.isExpanded) {
                    elApplicationForm.collapse()
                    application_form_card_iv.image = context?.resources?.getDrawable(R.drawable.ic_expand_more_black_24dp, null)
                } else {
                    if (!isApplicationFormDownloaded) {
                        callDigitalDocumentApi(DocumentTitles.APPLICATION_FORM.value)
                    }
                    elApplicationForm.expand()
                    application_form_card_iv.image = context?.resources?.getDrawable(R.drawable.ic_expand_less_black_24dp, null)
                }
            }

            view_ach_mandate -> {

                if (elAchMandate.isExpanded) {
                    elAchMandate.collapse()
                    ach_mandate_card_iv.image = context?.resources?.getDrawable(R.drawable.ic_expand_more_black_24dp, null)
                } else {
                    if (!isAchDownloaded) {
                        callDigitalDocumentApi(DocumentTitles.ACH_MANDATE_FORM.value)
                    }
                    elAchMandate.expand()
                    ach_mandate_card_iv.image = context?.resources?.getDrawable(R.drawable.ic_expand_less_black_24dp, null)
                }
            }

            view_document_form -> {

                if (elDocumentForm.isExpanded) {
                    elDocumentForm.collapse()
                } else {
                    if (!isDocChecklistDownloaded) {
                        callDigitalDocumentApi(DocumentTitles.DOCUMENT_CHECKLIST.value)
                    }
                    elDocumentForm.expand()
                }
            }

            view_document_loan_approval -> {

                if (elDocumentLoanApproval.isExpanded) {
                    elDocumentLoanApproval.collapse()
                } else {
                    if (!isApprovalLetterDownloaded) {
                        callDigitalDocumentApi(DocumentTitles.APPROVAL_LETTER.value)
                    }
                    elDocumentLoanApproval.expand()
                }
            }

            view_document_loan_agreement -> {

                if (elDocumentLoanAgreement.isExpanded) {
                    elDocumentLoanAgreement.collapse()
                } else {
                    if (!isAgreementLetterDownloaded) {
                        callDigitalDocumentApi(DocumentTitles.LOAN_AGREEMENT.value)
                    }
                    elDocumentLoanAgreement.expand()
                }
            }

            view_document_aadhar_consent -> {
                if (elDocumentAadharConsent.isExpanded) {
                    elDocumentAadharConsent.collapse()
                } else {
                    if (!isAddharConsentDownloaded) {
                        callDigitalDocumentApi(DocumentTitles.AADHAAR_CONSENT_LETTER.value)
                    }
                    elDocumentAadharConsent.expand()
                }
            }

            civ_ach_mandate -> {
                onDisplayDigitalDocuments(
                        GngUtility.getFileUriFromFileName(context, ACH_MANDATE), getString(R.string.ach_mandate))
            }

            civ_application_form -> {
                onDisplayDigitalDocuments(
                        GngUtility.getFileUriFromFileName(context, APPLICATION_FORM), getString(R.string.application_form))
            }

            civ_document_form -> {
                onDisplayDigitalDocuments(
                        GngUtility.getFileUriFromFileName(context, DOCUMENT_CHECKLIST), getString(R.string.document_checklist))
            }

            civ_document_loan_approval -> {
                onDisplayDigitalDocuments(
                        GngUtility.getFileUriFromFileName(context, APPROVAL_LETTER), getString(R.string.loan_approval_letter))
            }

            civ_document_loan_agreement -> {
                onDisplayDigitalDocuments(
                        GngUtility.getFileUriFromFileName(context, LOAN_AGREEMENT), getString(R.string.loan_agreement))
            }
            civ_document_aadhar_consent -> {
                onDisplayDigitalDocuments(
                        GngUtility.getFileUriFromFileName(context, AADHAR_CONSENT_LETTER), getString(R.string.aadhaar_consent))
            }


            btn_next -> {
                if (isScreenValidData()) {
                    onDisplayInvoiceScreen()
                }
            }

            btn_cancel_approval_letter -> {
                onApprovalLetterCancelClicked(deliveryOrderId, fileId)
            }
        }
    }


    private fun callDoDownloadApi() {
        val postIPARequest = PostIpaRequest()
        postIPARequest.refID = ApplicationRequestUtility.getInstance().referenceID
        dODownloadPresenter?.callAPI(postIPARequest, APIClientUtils(context), BuildConfig.FLAVOR)
    }

    fun callDigitalDocumentApi(fileName: String) {

        val digitalDocumentRequest = DownloadDigitalDocumentRequest()
        val header = ApplicationHeaderUtility.getInstance().header
        digitalDocumentRequest.refID = ApplicationRequestUtility.getInstance().referenceID
        digitalDocumentRequest.header = header
        digitalDocumentRequest.fileName = fileName
        this.fileName = fileName
        digitalDocumentPresenter?.callAPI(digitalDocumentRequest, APIClientUtils(context), BuildConfig.FLAVOR)
    }

    private fun initRecycler() {
        rv_delivery_orders.layoutManager = LinearLayoutManager(context)
        rv_delivery_orders.addItemDecoration(VerticalSpaceItemDecoration(8))

        doDocumentAdapter = DoDocumentsAdapter()
        rv_delivery_orders.adapter = doDocumentAdapter

        listDeliveryDocuments?.let {
            doDocumentAdapter?.swapData(it)
        }

        doDocumentAdapter?.onItemClick = { documentWithAssetDetails, adapterPosition ->
            onDisplayDigitalDocuments(
                    GngUtility.getFileUriFromFileName(context, doMap[documentWithAssetDetails]), getString(R.string.delivery_order))
        }
    }

//
    /**
     * Block for handling presenter callbacks
     */
    override fun onDODownloadSuccess(listDeliveryDocuments: List<DocumentWithAssetDetails>) {
        if (listDeliveryDocuments.isNotEmpty()) {
            isDoDownloaded = true
            delivery_order_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
            logEvents(context, GoogleAnalyticsEvents.DO_GENERATION, boolean = true)
            for (deliveryDocument in listDeliveryDocuments) {
                doMap[deliveryDocument] = deliveryDocument.assetModelMake + DO_DOCUMENT_POSTFIX
                GngUtility.storeFileInInternalStorage(context, deliveryDocument.byteCode, deliveryDocument.assetModelMake + DO_DOCUMENT_POSTFIX)
            }
            this.listDeliveryDocuments = listDeliveryDocuments
            doDocumentAdapter?.swapData(listDeliveryDocuments)
            this.deliveryOrderId = listDeliveryDocuments[0].doId
            this.fileId = listDeliveryDocuments[0].docID
        }
    }

    override fun onDODownloadFailed(failedMessage: String) {
        showErrorMessage(failedMessage, context, null)
        logEvents(context, GoogleAnalyticsEvents.DO_GENERATION, boolean = false)
    }

    override fun onDocumentFetchSuccess(document: Document) {
        when (fileName) {
            DocumentTitles.ACH_MANDATE_FORM.value -> {
                isAchDownloaded = true
                ach_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
                GngUtility.storeFileInInternalStorage(context, document.byteCode, ACH_MANDATE)
            }

            DocumentTitles.APPLICATION_FORM.value -> {
                isApplicationFormDownloaded = true
                application_form_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
                GngUtility.storeFileInInternalStorage(context, document.byteCode, APPLICATION_FORM)
            }

            DocumentTitles.DOCUMENT_CHECKLIST.value -> {
                isDocChecklistDownloaded = true
                checklist_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
                GngUtility.storeFileInInternalStorage(context, document.byteCode, DOCUMENT_CHECKLIST)
            }

            DocumentTitles.APPROVAL_LETTER.value -> {
                isApprovalLetterDownloaded = true
                loan_approval_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
                GngUtility.storeFileInInternalStorage(context, document.byteCode, APPROVAL_LETTER)
            }

            DocumentTitles.LOAN_AGREEMENT.value -> {
                isAgreementLetterDownloaded = true
                loan_agreement_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
                GngUtility.storeFileInInternalStorage(context, document.byteCode, LOAN_AGREEMENT)
            }


            DocumentTitles.AADHAAR_CONSENT_LETTER.value -> {
                isAddharConsentDownloaded = true
                consent_download.setImageDrawable(context?.getDrawable(R.drawable.ic_checked))
                GngUtility.storeFileInInternalStorage(context, document.byteCode, AADHAR_CONSENT_LETTER)
            }
        }
    }

    override fun onDocumentFetchFailed(failedMessage: String) {
        showErrorMessage(failedMessage, context, null)
    }

    override fun onResume() {
        super.onResume()
        if (!isApprovalLetterDownloaded) {
            callDigitalDocumentApi(DocumentTitles.APPROVAL_LETTER.value)
        }
    }
}