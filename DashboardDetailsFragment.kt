package com.softcell.gonogo.hdfctw.mvp.views.main

import ImageByIDContract
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.softcell.gonogo.BuildConfig
import com.softcell.gonogo.R
import com.softcell.gonogo.hdfctw.enums.PostIpaScreens
import com.softcell.gonogo.hdfctw.mvp.views.BottomSheetFilterDialog
import com.softcell.gonogo.hdfctw.mvp.views.OnFragmentActionsListener
import com.softcell.gonogo.hdfctw.mvp.views.main.adapter.DashboardDetailsAdapter
import com.softcell.gonogo.hdfctw.mvp.views.post_sanction.digital_document.DigitalDocumentsActivity
import com.softcell.gonogo.hdfctw.mvp.views.post_sanction.post_ipa.PostIPAActivity
import com.softcell.gonogo.utilities.ProgressDialogUtility
import com.softcell.soutilities.GngUtility
import com.softcell.soutilities.compound_view.gngx.GngSpinner
import com.softcell.soutilities.compound_view.gngx.GngTextInput
import com.softcell.soutilities.enums.PhoneType
import com.softcell.soutilities.enums.UploadDocumentFileType
import com.softcell.soutilities.log.gngx.GngLog
import com.softcell.soutilities.log.gngx.showErrorMessage
import com.softcell.soutilities.mvp.models.common.CheckApplicationStatus
import com.softcell.soutilities.mvp.models.common.GNGWorkflowConstant
import com.softcell.soutilities.mvp.models.common.ImagesDetails
import com.softcell.soutilities.mvp.models.common.KycImageDetails
import com.softcell.soutilities.mvp.models.dashboard.DashboardDetails
import com.softcell.soutilities.mvp.models.dashboard.DashboardRequest
import com.softcell.soutilities.mvp.models.do_display.Document
import com.softcell.soutilities.mvp.models.do_display.GetFileRequest
import com.softcell.soutilities.mvp.models.other_local_models.ImageMetaData
import com.softcell.soutilities.mvp.models.post_ipa.PostIPA
import com.softcell.soutilities.mvp.models.post_ipa.PostIpaRequest
import com.softcell.soutilities.mvp.models.submit_application.ApplicationRequest
import com.softcell.soutilities.mvp.presenters.dashboard.DashboardApplicationDetailsContract
import com.softcell.soutilities.mvp.presenters.dashboard.DashboardApplicationDetailsPresenter
import com.softcell.soutilities.mvp.presenters.dashboard.DashboardDetailsContract
import com.softcell.soutilities.mvp.presenters.dashboard.DashboardDetailsPresenter
import com.softcell.soutilities.mvp.presenters.download_image.ApplicationImagesListContract
import com.softcell.soutilities.mvp.presenters.download_image.ApplicationImagesListPresenter
import com.softcell.soutilities.mvp.presenters.download_image.ImageByIDPresenter
import com.softcell.soutilities.mvp.presenters.post_ipa.FetchPostIpaContract
import com.softcell.soutilities.mvp.presenters.post_ipa.FetchPostIpaPresenter
import com.softcell.soutilities.mvp.view.fragements.BaseValidationFragment
import com.softcell.soutilities.network.APIClientUtils
import com.softcell.soutilities.offine_image_store.ImagePendingDBStoreUtility
import com.softcell.soutilities.offine_image_store.SdCardUtility
import com.softcell.soutilities.requests_response.ApplicationHeaderUtility
import com.softcell.soutilities.requests_response.ApplicationRequestUtility
import com.softcell.soutilities.requests_response.PostIpaUtility
import com.softcell.soutilities.storage.SpUtils
import com.softcell.soutilities.validation.StringUtilities
import kotlinx.android.synthetic.hdfctw.fragment_dashboard_details.*


class DashboardDetailsFragment : BaseValidationFragment(), DashboardDetailsContract.View,
        GngSpinner.OnSpinnerItemSelected, GngTextInput.OnInputTextChanged, View.OnClickListener,
        DashboardApplicationDetailsContract.View, ApplicationImagesListContract.View,
        ImageByIDContract.View, BottomSheetFilterDialog.OnFilterDialogActions,
        OnQueryTextListener, FetchPostIpaContract.View {

    private var LOG_TAG = DashboardDetailsFragment::class.java.simpleName
    private var dashboardDetailsAdapter: DashboardDetailsAdapter? = null
    private var dashboardDetailsPresenter: DashboardDetailsPresenter? = null
    private val LAST_WEEK = 0
    private val LAST_MONTH = 1
    private val LAST_YEAR = 2
    private val ONE_WEEK_AGO = 7
    private val ONE_MONTH_AGO = 30
    private val ONE_YEAR_AGO = 365
    private var onFragmentActionsListener: OnFragmentActionsListener? = null
    private var dashboardApplicationDetailsPresenter: DashboardApplicationDetailsPresenter? = null
    private var fetchPostIpaPresenter: FetchPostIpaPresenter? = null
    private var applicationImageRequest: ApplicationImagesListPresenter? = null
    private var imageDetailsList: MutableList<ImagesDetails>? = null
    private var imageByIDPresenter: ImageByIDPresenter? = null
    private var imageMetaDataMap: MutableMap<String, ImageMetaData> = mutableMapOf()
    private var imageBitmap: Bitmap? = null
    private var dashboardDetails: DashboardDetails? = null
    private var progressDialogUtility: ProgressDialogUtility? = null
    private val NO_DATA_FOUND = "No data found against provided request."
    private var listApplications: List<DashboardDetails>? = null
    private var searchView: SearchView? = null
    private var allApplicationsList: List<DashboardDetails>? = null
    private var searchMenuItem: MenuItem? = null

    companion object {
        val LOG_TAG = DashboardDetailsFragment::class.java.simpleName
        var selectedFilter: Int = 0

        fun newInstance(): DashboardDetailsFragment {
            val args = Bundle()
            val dashboardDetailsFragment = DashboardDetailsFragment()
            dashboardDetailsFragment.arguments = args
            return dashboardDetailsFragment
        }
    }

    override fun initializeViews() {

    }

    override fun initializePresenter() {
        dashboardDetailsPresenter = DashboardDetailsPresenter()
        dashboardDetailsPresenter?.attachView(this)
        dashboardDetailsPresenter?.attachSharedPreference(SpUtils.getInstance(context))

        dashboardApplicationDetailsPresenter = DashboardApplicationDetailsPresenter()
        dashboardApplicationDetailsPresenter?.attachView(this)
        dashboardApplicationDetailsPresenter?.attachSharedPreference(SpUtils.getInstance(context))

        applicationImageRequest = ApplicationImagesListPresenter()
        applicationImageRequest?.attachView(this)
        applicationImageRequest?.attachSharedPreference(SpUtils.getInstance(context))

        imageByIDPresenter = ImageByIDPresenter()
        imageByIDPresenter?.attachView(this)
        imageByIDPresenter?.attachSharedPreference(SpUtils.getInstance(context))

        fetchPostIpaPresenter = FetchPostIpaPresenter()
        fetchPostIpaPresenter?.attachView(this)
        fetchPostIpaPresenter?.attachSharedPreference(SpUtils.getInstance(context))
    }

    override fun getFragmentLayout(): View? {
        return inflater?.inflate(R.layout.fragment_dashboard_details, ll_dashboard_parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_dashboard_details.layoutManager = LinearLayoutManager(context)
        rv_dashboard_details.itemAnimator = DefaultItemAnimator()
        setHasOptionsMenu(true)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        getDashboardSearchByRefId(GngUtility.getLastYearDateFromCurrentDate(), GngUtility.getCurrentDate(), query)
        GngUtility.hideSoftkeyboard(searchView, context)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (StringUtilities.isEmpty(newText)) {
            allApplicationsList?.let { onApplicationList(it) }
        } else {
            dashboardDetailsAdapter?.onSearchFilter(newText)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_dashboard_details, menu)

        searchMenuItem = menu?.findItem(R.id.menu_search)
        searchView = searchMenuItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)
        searchView?.queryHint = getString(R.string.search_here)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_filter ->
                context?.let {
                    val bottomSheetDialog = BottomSheetFilterDialog.newInstance(this)
                    bottomSheetDialog.show(fragmentManager, bottomSheetDialog.tag)
                    return true
                }
        }
        return super.onOptionsItemSelected(item)
    }

    fun attachFragmentActionsCallback(onFragmentActionsListener: OnFragmentActionsListener?) {
        this.onFragmentActionsListener = onFragmentActionsListener
    }

    override fun setDataIfPresent() {

        onFragmentActionsListener?.setActivityTitle(getString(R.string.dashboard))
        getDashboardDetailsBySearch(GngUtility.convertDateToString(GngUtility.filterDateSelection(ONE_WEEK_AGO)),
                GngUtility.getCurrentDate())

    }

    override fun onResume() {
        super.onResume()
        setDataIfPresent()
    }

    override fun isScreenValidData(): Boolean {
        return true
    }

    override fun onApplicationList(listApplications: List<DashboardDetails>) {
        this.listApplications = listApplications
        if (isVisible) {
            ImagePendingDBStoreUtility.getInstance().clearSyncedImagesData()

            populateDashboard(listApplications)

            dashboardDetailsAdapter?.onItemClick = { dashboardDetails, adapterPosition ->
                //call API for application detail
//                gtil_search.setTextOnEt("")
                this.dashboardDetails = dashboardDetails
                val checkApplicationRequest = CheckApplicationStatus()
                dashboardApplicationDetailsPresenter?.setRefId(dashboardDetails?.referenceID)
                dashboardApplicationDetailsPresenter?.callAPI(checkApplicationRequest, APIClientUtils(context), BuildConfig.FLAVOR)
            }

            rv_dashboard_details?.adapter?.notifyDataSetChanged()
        }
    }

    private fun populateDashboard(listApplications: List<DashboardDetails>?) {
        listApplications?.let {
            dashboardDetailsAdapter = DashboardDetailsAdapter(it.toMutableList())
            rv_dashboard_details?.adapter = dashboardDetailsAdapter
        }
    }

    override fun onApplicationListFailed(message: String) {
        showErrorMessage(message, context, null)
    }

    override fun onError(error: String) {
        showErrorMessage(error, context, null)
    }

    override fun onItemSelected(view: GngSpinner, position: Int, selectedKey: String, selectedValue: String) {

    }

    override fun onFilterSelected(filterText: String) {
        when (filterText) {
            getString(R.string.filter_last_week) -> getDashboardDetailsBySearch(GngUtility.convertDateToString(GngUtility
                    .filterDateSelection(ONE_WEEK_AGO)), GngUtility.getCurrentDate())

            getString(R.string.filter_last_month) -> getDashboardDetailsBySearch(GngUtility.convertDateToString(GngUtility
                    .filterDateSelection(ONE_MONTH_AGO)), GngUtility.getCurrentDate())

            getString(R.string.filter_last_year) -> getDashboardDetailsBySearch(GngUtility.convertDateToString(GngUtility
                    .filterDateSelection(ONE_YEAR_AGO)), GngUtility.getCurrentDate())

            getString(R.string.all_time) -> allApplicationsList?.let { onApplicationList(it) }
        }
    }

    override fun onTextChanges(gngTextInput: GngTextInput, text: String) {

    }

    override fun onClick(view: View?) {

    }

    private fun getDashboardDetailsBySearch(fromDate: String?, toDate: String?) {
        if (isVisible) {
            val dashboardRequest = DashboardRequest()
            dashboardRequest.skip = 0
            dashboardRequest.limit = 100
            dashboardRequest.fromDate = fromDate
            dashboardRequest.toDate = toDate
            ApplicationHeaderUtility.getInstance()?.product?.name?.let {
                dashboardDetailsPresenter?.setProduct(it)
                dashboardDetailsPresenter?.callAPI(dashboardRequest, APIClientUtils(context), BuildConfig.FLAVOR)
            } ?: kotlin.run {
                showErrorMessage(getString(R.string.product_not_found), context, null)
            }
        }
    }

    private fun getDashboardSearchByRefId(fromDate: String?, toDate: String?, refId: String?) {
        val dashboardRequest = DashboardRequest()
        dashboardRequest.skip = 0
        dashboardRequest.limit = 100
        dashboardRequest.refId = refId
        dashboardRequest.fromDate = fromDate
        dashboardRequest.toDate = toDate
        dashboardDetailsPresenter?.setProduct(ApplicationHeaderUtility.getInstance().product.name)
        dashboardDetailsPresenter?.callAPI(dashboardRequest, APIClientUtils(context), BuildConfig.FLAVOR)
    }

    override fun getDataFromViews() {}

    override fun onApplication(application: ApplicationRequest) {
        val appSource = ApplicationHeaderUtility.getInstance().header.applicationSource
        val sourceId = ApplicationHeaderUtility.getInstance().header.sourceId
        val dealerName = ApplicationHeaderUtility.getInstance().header.dealerName
        val dealerId = ApplicationHeaderUtility.getInstance().header.dealerId
        val product = ApplicationHeaderUtility.getInstance().header.product

        //save in header
        ApplicationHeaderUtility.clearSingleton()
        ApplicationHeaderUtility.getInstance().header = application.header
        ApplicationHeaderUtility.getInstance().header.applicationSource = appSource
        ApplicationHeaderUtility.getInstance().header.sourceId = sourceId
        ApplicationHeaderUtility.getInstance().header.dealerName = dealerName
        ApplicationHeaderUtility.getInstance().header.dealerId = dealerId
        ApplicationHeaderUtility.getInstance().header.product = product

        //save in shared pref
        SpUtils.getInstance(context).dealerName = application.header.dealerName
        SpUtils.getInstance(context).dealerId = application.header.dealerId

        ApplicationRequestUtility.clearSingleton()
        ApplicationRequestUtility.getInstance().applicationRequest = application

        //set to SdCardUtility
        SdCardUtility.setMobileNumber(ApplicationRequestUtility.getInstance().getMobileNumber(PhoneType.PERSONAL_MOBILE.toString()))
        SdCardUtility.setSubFolder(ApplicationRequestUtility.getInstance().getMobileNumber(PhoneType.PERSONAL_MOBILE.toString()))
        SdCardUtility.setReferenceId(ApplicationRequestUtility.getInstance().referenceID)

        callApiForPostIpaData()
    }

    private fun callApiForPostIpaData() {
        val postIpaRequest = PostIpaRequest()
        postIpaRequest.refID = ApplicationRequestUtility.getInstance().referenceID

        fetchPostIpaPresenter?.callAPI(postIpaRequest, APIClientUtils(context), BuildConfig.FLAVOR)
    }

    override fun onPostIpaFetchSuccess(listApplications: List<PostIPA>?) {
        PostIpaUtility.setMultiAssetDetails(listApplications)
        getApplicationImagesList()
    }

    override fun onPostIpaFetchFailure(error: String?) {
        if (!NO_DATA_FOUND.equals(error, true)) {
            showErrorMessage(error, context, null)
        }
        getApplicationImagesList()
    }

    override fun onApplicationFailed(message: String) {
        showErrorMessage(message, context, null)
    }

    private fun getApplicationImagesList() {
        val checkApplicationRequest = CheckApplicationStatus()
        applicationImageRequest?.callAPI(checkApplicationRequest, APIClientUtils(context), BuildConfig.FLAVOR)
    }

    private fun showProgressDialog() {
        progressDialogUtility = ProgressDialogUtility(context)
        progressDialogUtility?.setTitle("Please wait. Downloading images.")
        progressDialogUtility?.setCanceledOnTouchOutside(false)
        progressDialogUtility?.show()
    }

    override fun onApplicationImageListSuccess(listApplications: List<KycImageDetails>) {
        if (listApplications.isNotEmpty()) {
            showProgressDialog()
        }

        for (i in 0 until listApplications.size) {
            listApplications[i].imageMap?.let {
                if (listApplications[i].imageMap.isNotEmpty()) {
                    this.imageDetailsList = listApplications[i].imageMap
                    getImageBase64ById(listApplications[i].applicantId)
                }
            }
        }
    }

    override fun onApplicationImageListFailed(message: String) {
        showErrorMessage(message, context, null)
        populateDashboard(listApplications)
    }

    private fun getImageBase64ById(applicantId: String) {

        imageDetailsList?.let {
            var imageMetaData: ImageMetaData?

            for (i in 0 until it.size) {

                //meta-data class for our reference
                imageMetaData = ImageMetaData()
                imageMetaData.applicantId = applicantId
                imageMetaData.imageId = it[i].imageId
                imageMetaData.imageType = it[i].imageType
                imageMetaData.imageName = it[i].imageTitle
                imageMetaData.imageCategory = it[i].imgCat

                //map to maintain data against imageId
                imageMetaDataMap[it[i].imageId] = imageMetaData

                if (UploadDocumentFileType.APPLICANT_PHOTO.value.equals(it[i].imageTitle, true)) {
                    //send image id in request obj for our future reference
                    val fileRequest = GetFileRequest()
                    fileRequest.imageFileID = it[i].imageId
                    imageByIDPresenter?.callAPI(fileRequest, APIClientUtils(context), BuildConfig.FLAVOR)
                }
            }
        }
    }

    override fun onImageBase64Success(document: Document) {
        //fetch data from map
        var applicantId: Int? = 0
        val imageMetaData = imageMetaDataMap[document.docID]
        try {
            applicantId = imageMetaData?.applicantId?.toInt()?.minus(1)
        } catch (nfe: NumberFormatException) {
            GngLog.logErrorWithPartition(LOG_TAG, "applicantId NumberFormatException ---> " + nfe.printStackTrace())
        }

        imageBitmap = GngUtility.getBitmapFromString(document.byteCode)

        when (imageMetaData?.imageType) {
            UploadDocumentFileType.APPLICANT_PHOTO.value ->
                SdCardUtility.addOrUpdateImage(imageBitmap, UploadDocumentFileType.APPLICANT_PHOTO.value,
                        imageMetaData.imageType, "", applicantId!!, true, imageMetaData.imageCategory)
        }

        imageMetaDataMap.clear()
        bitmap?.recycle()
        checkAndNavigateToApplicationDetails()
    }

    override fun onImageBase64Failed(failedMessage: String) {
        showErrorMessage(failedMessage, context, null)
        populateDashboard(listApplications)
    }

    private fun checkAndNavigateToApplicationDetails() {
        progressDialogUtility?.dismiss()

     //   PostIPAActivity.launch(context, PostIpaScreens.SanctionLetter, true)


        if (imageMetaDataMap.isEmpty()) {
            //check on stage of application and navigate accordingly

            if (GNGWorkflowConstant.CR_Q.toFaceValue().equals(dashboardDetails?.stages, true) &&
                    ApplicationRequestUtility.getInstance().isQdeDecision) {
                ApplicationDetailsActivity.launch(context, true, dashboardDetails)

            } else if (GNGWorkflowConstant.CR_Q.toFaceValue().equals(dashboardDetails?.stages, true) &&
                    ApplicationRequestUtility.getInstance().isQdeDecision) {
                ApplicationDetailsActivity.launch(context, true, dashboardDetails)

            } else if (GNGWorkflowConstant.CR_Q.toFaceValue().equals(dashboardDetails?.stages, true) &&
                    !ApplicationRequestUtility.getInstance().isQdeDecision) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.APRV.toFaceValue().equals(dashboardDetails?.stages, true) &&
                    ApplicationRequestUtility.getInstance().isQdeDecision) {
                ApplicationDetailsActivity.launch(context, true, dashboardDetails)

            } else if (GNGWorkflowConstant.APRV.toFaceValue().equals(dashboardDetails?.stages, true) &&
                    ApplicationRequestUtility.getInstance().isQdeDecision) {
                ApplicationDetailsActivity.launch(context, true, dashboardDetails)

            } else if (GNGWorkflowConstant.APRV.toFaceValue().equals(dashboardDetails?.stages, true) &&
                    !ApplicationRequestUtility.getInstance().isQdeDecision) {
                ApplicationDetailsActivity.launch(context, true, dashboardDetails)

            } else if (GNGWorkflowConstant.DCLN.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.CNCLD.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.CR_H.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.APPROVED.toFaceValue().equals(dashboardDetails?.status, true) &&
                    GNGWorkflowConstant.POST_DECISION_DATA_ENTRY.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, true, dashboardDetails)

            } else if (GNGWorkflowConstant.APPROVED.toFaceValue().equals(dashboardDetails?.status, true) &&
                    GNGWorkflowConstant.INV_GNR.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.ON_HOLD.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.LMS_APRV.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.LMS_ERROR.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.PMT_APRV.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.PMT_ERROR.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.LMS_DISB.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.OPS_H.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.APRV_LL.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.DO_RESET_REQ.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.DO_RESET_RECOM.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.FL_HND.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else if (GNGWorkflowConstant.LMS_PND.toFaceValue().equals(dashboardDetails?.stages, true)) {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)

            } else {
                ApplicationDetailsActivity.launch(context, false, dashboardDetails)
            }
        }
    }
}