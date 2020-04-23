package com.softcell.gonogo.hdfctw.mvp.views.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import com.softcell.gonogo.R
import com.softcell.gonogo.exception.DefaultExceptionHandler
import com.softcell.gonogo.hdfctw.enums.PostIpaScreens
import com.softcell.gonogo.hdfctw.mvp.views.apply.dde.*
import com.softcell.gonogo.hdfctw.mvp.views.apply.dde.ProductDetailsFragment
import com.softcell.gonogo.hdfctw.mvp.views.apply.offers.OffersActivity
import com.softcell.gonogo.hdfctw.mvp.views.apply.qde.*
import com.softcell.gonogo.hdfctw.mvp.views.common.SessionManagementActivity
import com.softcell.gonogo.hdfctw.mvp.views.post_sanction.digital_document.DigitalDocumentsActivity
import com.softcell.gonogo.hdfctw.mvp.views.post_sanction.digital_document.DisplayDigitalDocumentFragment
import com.softcell.gonogo.hdfctw.mvp.views.post_sanction.digital_document.InvoiceFragment
import com.softcell.gonogo.hdfctw.mvp.views.post_sanction.post_ipa.PDCDetailsFragment
import com.softcell.soutilities.mvp.models.asset.AssetDetails
import com.softcell.soutilities.mvp.models.common.Eligibility
import com.softcell.soutilities.mvp.models.dashboard.DashboardDetails
import com.softcell.soutilities.mvp.models.submit_application.CancellationHistory
import com.softcell.soutilities.mvp.presenters.pdc_details.PDCDetailsContract

/**
 * Created by rahulahuja on 11/04/19.
 */
abstract class ApplicationDetailsActivity : SessionManagementActivity(),
        ApplicationOverviewActionsListener, ApplicationDetailsActionsListener {

    private var LOG_TAG = ApplicationDetailsActivity::class.java.simpleName
    private var isFieldsEnable: Boolean = false
    private var applicationOverviewFragment: ApplicationOverviewFragment? = null
    private var applicationDetailsFragment: ApplicationDetailsFragment? = null
    private var personalDetailsFragment: PersonalDetailsFragment? = null
    private var addressDetailsFragment: AddressDetailsFragment? = null
    private var workDetailsFragment: WorkDetailsFragment? = null
    private var productDetailsFragment: ProductDetailsFragment? = null
    private var qdeProductDetailsFragment: com.softcell.gonogo.hdfctw.mvp.views.apply.qde.ProductDetailsFragment? = null
    private var schemesLoansFragment: SchemesLoansFragment? = null
    private var addOnProductsFragment: AddOnProductsFragment? = null
    private var documentUploadFragment: DocumentUploadFragment? = null
    private var referenceFragment: ReferenceFragment? = null
    private var bankVerificationFragment: BankVerificationFragment? = null
    private var pdcDetailsFragment: PDCDetailsFragment? = null
    private var dashboardDetails: DashboardDetails? = null
    private var addOnDetailsFragment: AddOnDetailsFragment? = null
    private var documentReworkFragment: DocumentReworkFragment? = null
    private var digitalDocumentOverviewFragment: DigitalDocumentOverviewFragment? = null
    private var displayDigitalDocumentFragment: DisplayDigitalDocumentFragment? = null
    private var debitCreditCardFragment: DebitCreditCardFragment? = null
    private var qdeWorkDetailsFragment: QdeWorkDetailsFragment? = null
    private var schemeDetailsFragment: SchemeDetailsFragment? = null
    private var qdeMainFragment: QdeMainFragment? = null
    private var loanBankingInfoFragment: LoanBookingInfoFragment? = null
    private var invoiceFragment: InvoiceFragment? = null

    companion object {
        private val IS_FIELDS_ENABLE = "isFieldsEnable"
        private val DASHBOARD_DETAILS = "dashboardDetails"

        fun launch(context: Context?, isFieldsEnable: Boolean, dashboardDetails: DashboardDetails?) {
            val intent = Intent(context, ApplicationDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(DASHBOARD_DETAILS, dashboardDetails)
            bundle.putBoolean(IS_FIELDS_ENABLE, isFieldsEnable)
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(DefaultExceptionHandler(this))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        dashboardDetails = intent?.extras?.getParcelable(DASHBOARD_DETAILS)
        isFieldsEnable = intent?.extras?.getBoolean(IS_FIELDS_ENABLE) ?: false

        displayApplicationOverviewScreen()
    }

    override fun initializeViews() {
        super.initializeViews()
        hideBottomNavigation()
    }

    private fun displayApplicationOverviewScreen() {
        applicationOverviewFragment = ApplicationOverviewFragment.newInstance(true, dashboardDetails)
        applicationOverviewFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(applicationOverviewFragment, false, ApplicationOverviewFragment.LOG_TAG, false)
    }

    private fun displayApplicationDetailsScreen() {
        applicationDetailsFragment = ApplicationDetailsFragment.newInstance(isFieldsEnable, dashboardDetails)
        applicationDetailsFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(applicationDetailsFragment, true, ApplicationDetailsFragment.LOG_TAG, true)
    }

    private fun displayPersonalDetails() {
        if (personalDetailsFragment == null) {
            personalDetailsFragment = PersonalDetailsFragment.newInstance(false)
        }
        personalDetailsFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(personalDetailsFragment, true, PersonalDetailsFragment.LOG_TAG, true)
    }

    private fun displayAddressDetails() {
        if (addressDetailsFragment == null) {
            addressDetailsFragment = AddressDetailsFragment.newInstance(false)
        }
        addressDetailsFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(addressDetailsFragment, true, AddressDetailsFragment.LOG_TAG, true)
    }

    private fun displayWorkDetails() {
        if (workDetailsFragment == null) {
            workDetailsFragment = WorkDetailsFragment.newInstance(false)
        }
        shouldShowBackButton(true)
        workDetailsFragment?.attachFragmentActionsCallback(this)
        replaceFragment(workDetailsFragment, true, WorkDetailsFragment.LOG_TAG, true)
    }

    private fun displayProductDetails() {
        if (productDetailsFragment == null) {
            productDetailsFragment = ProductDetailsFragment.newInstance(false)
        }
        shouldShowBackButton(true)
        productDetailsFragment?.attachFragmentActionsCallback(this)
        replaceFragment(productDetailsFragment, true, ProductDetailsFragment.LOG_TAG, true)
    }

    private fun displaySchemeLoan() {
        if (schemesLoansFragment == null) {
            schemesLoansFragment = SchemesLoansFragment.newInstance(isFieldsEnable, true)
        }
        shouldShowBackButton(true)
        schemesLoansFragment?.attachFragmentActionsCallback(this)
        replaceFragment(schemesLoansFragment, true, SchemesLoansFragment.LOG_TAG, true)
    }

    private fun displayAddOnProducts() {
        if (addOnProductsFragment == null) {
            addOnProductsFragment = AddOnProductsFragment.newInstance(isFieldsEnable, true)
        }
        addOnProductsFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(addOnProductsFragment, true, AddOnProductsFragment.LOG_TAG, true)
    }

    private fun displayDocumentUpload() {
        if (documentUploadFragment == null) {
            documentUploadFragment = DocumentUploadFragment.newInstance(false)
        }
        shouldShowBackButton(true)
        documentUploadFragment?.attachFragmentActionsCallback(this)
        replaceFragment(documentUploadFragment, true, DocumentUploadFragment.LOG_TAG, true)
    }

    private fun displayReferences() {
        if (referenceFragment == null) {
            referenceFragment = ReferenceFragment.newInstance(false)
        }
        referenceFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(referenceFragment, true, ReferenceFragment.LOG_TAG, true)
    }

    private fun displayBankingDetails() {
        bankVerificationFragment = BankVerificationFragment.newInstance(false)
        bankVerificationFragment?.attachFragmentACtionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(bankVerificationFragment, true, BankVerificationFragment.LOG_TAG, true)
    }

    private fun displayPdcDetails() {
        if (pdcDetailsFragment == null) {
            pdcDetailsFragment = PDCDetailsFragment.newInstance( false)
        }
        pdcDetailsFragment = PDCDetailsFragment.newInstance(false)
        pdcDetailsFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(pdcDetailsFragment, true, PDCDetailsFragment.LOG_TAG, true)    }



    private fun displayAddOnDetails(assetDetails: AssetDetails?, adapterPosition: Int) {
        if (addOnDetailsFragment == null) {
            addOnDetailsFragment = AddOnDetailsFragment.newInstance(false, assetDetails, adapterPosition, true)
        }
        shouldShowBackButton(true)
        addOnDetailsFragment?.attachFragmentActionsCallback(this)
        replaceFragment(addOnDetailsFragment, true, AddOnDetailsFragment.LOG_TAG, true)
    }

    private fun displayDocumentRework(dashboardDetails: DashboardDetails?) {
        documentReworkFragment = DocumentReworkFragment.newInstance(true, dashboardDetails)
        shouldShowBackButton(true)
        documentReworkFragment?.attachFragmentActionsCallback(this)
        replaceFragment(documentReworkFragment, true, DocumentReworkFragment.LOG_TAG, true)
    }

    private fun displayDigitalDocumentOverview() {
        title = getString(R.string.digitised_documents)
        digitalDocumentOverviewFragment = DigitalDocumentOverviewFragment.newInstance(false)
        shouldShowBackButton(true)
        digitalDocumentOverviewFragment?.attachFragmentActionsCallback(this)
        replaceFragment(digitalDocumentOverviewFragment, true, DigitalDocumentOverviewFragment.LOG_TAG, true)
    }

    private fun displayDigitalDocument(docUri: Uri, documentTitle: String?) {
        displayDigitalDocumentFragment = DisplayDigitalDocumentFragment.newInstance(docUri, documentTitle)
        displayDigitalDocumentFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(displayDigitalDocumentFragment, true, DisplayDigitalDocumentFragment.LOG_TAG, true)
    }


    private fun displayDebitCreditFragment() {
        debitCreditCardFragment = DebitCreditCardFragment.newInstance(false)
        debitCreditCardFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(debitCreditCardFragment, true, DebitCreditCardFragment.LOG_TAG, true)
    }

    private fun displayQdeWorkDetailsFragment() {
        qdeWorkDetailsFragment = QdeWorkDetailsFragment.newInstance(false)
        qdeWorkDetailsFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(qdeWorkDetailsFragment, true, QdeWorkDetailsFragment.LOG_TAG, true)
    }

    private fun displaySchemeLoanDetails(assetDetails: AssetDetails?) {
        schemeDetailsFragment = SchemeDetailsFragment.newInstance(false, assetDetails)
        shouldShowBackButton(true)
        schemeDetailsFragment?.attachFragmentActionsCallback(this)
        replaceFragment(schemeDetailsFragment, true, SchemeDetailsFragment.LOG_TAG, true)
    }

    private fun displayQdeMainFragment() {
        qdeMainFragment = QdeMainFragment.newInstance(false)
        shouldShowBackButton(true)
        qdeMainFragment?.attachFragmentActionsCallback(this)
        replaceFragment(qdeMainFragment, true, QdeMainFragment.LOG_TAG, true)
    }

    private fun displayQdeProductDetailsFragment() {
        qdeProductDetailsFragment = com.softcell.gonogo.hdfctw.mvp.views.apply.qde.ProductDetailsFragment.newInstance(false)
        qdeProductDetailsFragment?.attachFragmentActionsCallback(this)
        shouldShowBackButton(true)
        replaceFragment(qdeProductDetailsFragment, true, com.softcell.gonogo.hdfctw.mvp.views.apply.qde.ProductDetailsFragment.LOG_TAG, true)
    }


    private fun displayLoanBankingInfoFragment() {
        loanBankingInfoFragment = LoanBookingInfoFragment.newInstance(false)
        loanBankingInfoFragment?.attachFragmentActionsCallback(this)
        replaceFragment(loanBankingInfoFragment, true, LoanBookingInfoFragment.LOG_TAG, true)
    }

    private fun displayInvoiceFragment() {
        invoiceFragment = InvoiceFragment.newInstance(false)
        title = getString(R.string.invoice_details)
        replaceFragment(invoiceFragment, true, InvoiceFragment.LOG_TAG, true)
    }



    override fun setActivityTitle(screenTitle: String?) {
        title = screenTitle
    }

    override fun onActionPopFragment() {
        popFragmentFromBackstack()
    }

    private fun popFragmentFromBackstack() {
        supportFragmentManager?.popBackStack()
    }

    override fun onApplicationDetailsSelected() {
        displayApplicationDetailsScreen()
    }

    override fun onRestartApplicationSelected() {
        DetailedDataEntryActivity.launch(this, isFieldsEnable)
        finish()
    }

    override fun onRestartLowerLimitApplication(dashboardDetails: DashboardDetails?) {
        DetailedDataEntryActivity.launch(this, dashboardDetails, isFieldsEnable)
        finish()
    }

    override fun onRestartShowOfferList(isQdeFlow: Boolean, offersList: ArrayList<Eligibility>?) {
        OffersActivity.launch(this, isQdeFlow, true, offersList)
        finish()
    }

    override fun onDigitisedDocumentsSelected() {
        displayDigitalDocumentOverview()
    }

    override fun onDocumentsOnHoldSelected(dashboardDetails: DashboardDetails?) {
        displayDocumentRework(dashboardDetails)
    }


    override fun onPersonalDetailsSelected() {
        displayPersonalDetails()
    }

    override fun onAddressDetailsSelected() {
        displayAddressDetails()
    }

    override fun onProductDetailsSelected() {
        displayProductDetails()
    }

    override fun onDisplayStatusSelected(serviceList: ArrayList<String>?) {}
    override fun onApplicationStatusSelected() {}

    override fun onWorkDetailsSelected() {
        displayWorkDetails()
    }

    override fun onSchemeLoanSelected() {
        displaySchemeLoan()
    }

    override fun onAddOnProductSelected() {
        displayAddOnProducts()
    }

    override fun onDocumentUploadSelected() {
        displayDocumentUpload()
    }

    override fun onReferencesSelected() {
        displayReferences()
    }

    override fun onBankingDetailsSelected() {
        displayBankingDetails()
    }

    override fun onProductSelectedForAddOn(assetDetails: AssetDetails?, adapterPosition: Int) {
        displayAddOnDetails(assetDetails, adapterPosition)
    }

    override fun onProductSelectedForScheme(assetDetails: AssetDetails?) {
        displaySchemeLoanDetails(assetDetails)
    }

    override fun onAddOtherDocumentSelected() {}


    override fun onApplicationStatus() {}

    override fun onRestartHandover() {
        DigitalDocumentsActivity.launch(this, PostIpaScreens.OtherDetails, true)
        finish()
    }

    override fun onDisplayDigitalDocument(docUri: Uri, documentTitle: String?) {
        displayDigitalDocument(docUri, documentTitle)
    }


    override fun onRestartDigitalDocuments(postIpaScreens: PostIpaScreens, isFieldsEnable: Boolean?) {
        DigitalDocumentsActivity.launch(this, postIpaScreens, isFieldsEnable)
        finish()
    }


    override fun onQdeWorkDetailsSelected() {
        displayQdeWorkDetailsFragment()
    }


    override fun onDebitCreditCardSelected() {
        displayDebitCreditFragment()
    }

    override fun onDoCancellationSelected(cancellationHistory: CancellationHistory?) {
        // Do nothing
    }

    override fun onDoCacellationHistorySelected() {
        // Do nothing
    }

    override fun onBasicDemographicSelected() {
        displayQdeMainFragment()
    }

    override fun onQdeProductDetailsSelected() {
        displayQdeProductDetailsFragment()
    }

    override fun displayDDE() {
    }

    override fun onLoanBookingInformationSelected() {
        displayLoanBankingInfoFragment()
    }

    override fun onInvoiceDetailsSelected() {
        displayInvoiceFragment()
    }

    override fun onDisplaySanctionLetter() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayOtherDigitalForms() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpdateApprovalLetterStage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayInvoiceScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayReferenceScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayEMandateScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayInsuranceDetailsScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayChassisDetailsScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayRTODetailsScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onApprovalLetterCancelClicked(approvalLetterId: String?, fileId: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisplayPDCDetailsScreen() {
        displayPdcDetails()
    }




}