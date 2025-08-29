## Overview:

The primary goal of this project is to generate large datasets to simulate the data found in a hospital environment.
Currently, that encompasses both imaging studies stored in DICOM format and corresponding radiology reports stored
in HL7 v2 format. For the generated DICOM data, there are significant limitations to keep in mind, as the data is
primarily modeled at the study, series, and equipment levels.

The user provides a specification YAML file to define specifics about the dataset. The generation process is designed to
scale to a very large population, so this works in 2 stages. First, the generator creates internal representations of
the patients, studies, and reports and writes them to intermediate YAML files. Then, each individual YAML file is
read back into memory and the generator writes out DICOM and HL7 data. The primary benefits of this setup are:

* Allowing the generator to create possibly millions of patients without needing to keep all of them in memory at once.
* Allowing a resume process on stage 2 if writing data fails part of the way through.

Running the generator can be accomplished through gradle. To launch stage 1, you can run:
```console
$ ./gradlew generateSpecs -Pconfig=local.yaml -PwriteToFiles=true
```
The `config` parameter is how to provide your specification file. If left out, the generator will instead load the
default configuration from [basicRequest.yaml](src/main/resources/basicRequest.yaml). The `writeToFiles`
parameter defaults to `false`, and controls launching stage 2 automatically.

Stage 2 can also be launched explicitly:
```console
$ ./gradlew writeFiles -PstandardSpecs=4
```
Exactly one of `standardSpecs` or `batchSpecs` should be provided to specify which "batch" files should be written
to DICOM and HL7. `batchSpecs` specifies a comma-separated list of batch files to write out. A reasonable example
could be `-PbatchSpecs=batches/batch_0.yaml,batches/batch_1.yaml`. `standardSpecs` is a shortcut to instead specify
standard batches 0 through the integer provided. So, providing `-PstandardSpecs=1` would be equivalent to the
previous example.

## Config:
Most of the configuration options are self-explanatory in the [default](src/main/resources/basicRequest.yaml) configuration,
but there are a few booleans that could use explanation:

* `generateRadiologyReports`: by default, this is set to `false` so no HL7 reports will be generated. You can set it
   to `true` to generate reports, but these are generated with GPT, so you must specify the corresponding Azure environment variables:
   `AZURE_OPENAI_KEY`.
* `includeBinariesInPrivateElements`: if set to `true`, some nonsense data of varying size (but reaching up to around 4MB)
   will be added to a DICOM private element in `(0015,1020)`. The intended use-cases are for creating larger files to test
   ingestion capabilities or DICOM header extraction.
* `includePixelData`: by default, this is set to `false`, meaning all DICOM instances generated will not include pixel data.
   If set to `true`, several of the general instance types will include pixel data (and related metadata such as Rows, Columns, etc.)
   will be copied in from a known reasonable source. Pixel sources are selected based on both the modality of the instance to be
   generated and body part specified in the headers. Instances that are not images (such as RTSTRUCT or KO) will not have pixel data added.
   This setting is disabled by default as the data with the headers will not match the pixel data well at all, but it can be enabled in case
   suboptimal pixel data is acceptable. The known pixel data is downloaded and stored in a folder called `local_cache` so that it will only
   need to be downloaded once. Currently supported images are:
    * chest - CR
    * breast - MG
    * abdomen - MR
    * brain - MR
    * chest - MR
    * head - MR
    * heart - MR
    * pelvis - MR
    * heart - NM
    * US (body part is not usually referenced in DICOM)
    * aorta - XA

## Study Types implemented:

*First note that elements at the instance level are primarily not implemented. For example, magnetic field strength and scanning sequence are very important for MR data, but all of these types of elements have to be modeled, interrelated, and randomized based on SOP class. These elements are in general not even included in the below lists, since they would take a very, very large amount of work to implement.*

Implemented:

* **MG**: Mammogram with randomly chosen single view position.
* **MG**: Mammogram with 4 standardized view positions.
* **MR**: A complex MRI protocol consisting of:
    + Transverse Localizer
    + Transverse T1w
    + Transverse T2w
    + Transverse DWI
    + Sagittal DTI
    + Transverse FLAIR
    + Sagittal T2\*
    + Transverse T1w with contrast
    + Coronal BOLD
* **MR**: A simple fast MRI protocol consisting of:
    + Transverse Localizer
    + Transverse T2w
    + Transverse T1w
* **MR:** A copy of the previous protocol except it's acquired at one of a few different outside hospitals.
* **MR**: A diffusion and perfusion heavy brain-only MRI protocol:
    + Sagittal Localizer
    + Sagittal T1w
    + Transverse SWI
    + Sagittal DCE
    + Sagittal ADC
    + Sagittal DSC
* **PT,CT**: A PET/CT simulated from a combined dual-modality Siemens scanner with series:
    + Topogram
    + Siemens (Proprietary SOP Class) VOI capture
    + CT w/ Attenuation Correction
    + PT w/ Attenuation Correction
    + PT w/o Attenuation Correction
    + "Patient Protocol" secondary capture
* **CR**: An xray simulated from a mobile Philips scanner with a single anteroposterior view of the chest or abdomen
* **DX**: An xray simulated from a GE scanner with a single view of one of the following body parts (selected randomly):
    + Abdomen
    + Cervical Spine
    + Chest
    + Skull
    + Spine
    + Thoracic Spine
* **NM**: A myocardial perfusion study with resting and gated series simulated from a GE scanner.
* **NM**: A copy of the previous protocol except it's acquired at an outside hospital.
* **US:** A single series standard ultrasound simulated from either an Acuson or Philips scanner.
* **XA:** An xray angiography/fluoroscopy study simulated from either a Philips or Siemens scanner with:
    + 10 series for studies simulated from the main hospital
    + 15 series for studies simulated from an outside institution
* **PT,MR,SR:** A PET-MR study simulated from a Siemens PET-MR scanner. Also includes a "Phoenix Evidence Document" structured report (without any of the structured report data, see the note in italics).
* **<Various>:** Digitized film secondary capture objects for two different types of acquisition:
    + **MG/OT/SC:** A digitized film mammogram with a modality value selected randomly from the list.
    + **CR/DX/OT/SC/XR:** A digitized film xray with a modality value selected randomly from the list.
* **<Various>**: Several of the previous study types are also simulated from hospitals outside the US, including Japan, Korea, and Greece. Names of both patients and physicians are localized and written in non-Latin character sets.
* **MR,RTSTRUCT,KO:** An MR study with contours added later via RTSTRUCT object (note that the contours are in the equivalent of an instance level attribute so the contours themselves are not actually included), and then a Key Object note added later by an intermediate PACS.
* **MG,PR:** A mammogram with annotations added later.

## Perturbations

In the real world (i.e. on a clinical PACS), you'll find the same thing encoded multiple ways at different times, or sometimes missing entirely. To be as realistic as possible, there are some "perturbations" intentionally introduced in the data:

1. Patient name. A patient name is written "Last^First" most of the time, but a small percent of the time, they may have their middle initial or full middle name added, and/or the whole string will be coerced to uppercase. (There's also international scanners which include the ideographic/phonetic name representations).
2. Ethnic group: a patient is assigned one ethnic group, but it may be entered differently for each study to match what the technician is entered (e.g. on study 1, the patient is "White", but on study 2, the patient is "W").
3. Patient age is sometimes left out of the DICOM for a study. The percent of this happening is configurable.
4. Referring Physician, Performing Physician(s), and Operators are sometimes included, and sometimes suppressed at the study level.
5. Patient Weight and Patient Size (height) are configurable for the probability to include them. Additionally, they're further configurable on probability to encode them as "0.0" to match the real world.

## Temporal Deployment

The `helm/` directory contains a helm chart to deploy the data generator as a [temporal](https://temporal.io/) workflow. When deploying the helm chart,
you should specify the path to the directory to mount into the data generator pod with `--set volumes[0].hostPath.path=$MOUNT_PATH` . If you are going to use
the capability to generate report text with GPT, you should also set `env.secret.AZURE_OPENAI_KEY` . When helm installs
the chart, those values will be used to create Kubernetes secrets to expose within the data generator pod to allow the user to generate GPT reports without
needing to specify the credentials at runtime. A full example installation command might look like:

```shell
helm upgrade --install -n data-generator data-generator helm/ -f helm/values.yaml --set env.secret.AZURE_OPENAI_KEY=$AZURE_OPENAI_KEY --set volumes[0].hostPath.path=/scout/generator --set volumes[1].hostPath.path=/scout/output
```

Once deployed as a temporal worker, you can launch a data generation workflow in the temporal UI by clicking the "Start Workflow" button and filling out the form:
1. For "Workflow ID", enter a unique string or use the "Random UUID" button.
2. For "Task Queue", specify `data-generator`.
3. For "Workflow Type", specify `GenerateDatasetWorkflow`.
4. For Input > Data, provide your input parameters as a JSON object (see below).
5. For Input > Encoding, select `json/plain`.

The JSON input corresponds to a serialized version of [GenerateDatasetInput](src/main/groovy/edu/washu/tag/generator/temporal/model/GenerateDatasetInput.groovy).
Currently, the properties of that object are:
- `specificationParametersPath`: (_Required_) the full path to the [config](#config) YAML which you wish to use to generate a dataset.
- `writeDicom`: (Optional) a boolean for controlling if DICOM files should be written for the dataset. Defaults to `true`.
- `writeHl7`: (Optional) a boolean for controlling if HL7 files (and aggregated "hl7ish" log files) should be written for the dataset corresponding to radiology reports. Defaults to `true`.
- `concurrentExecution`: (Optional) an integer for defining how many subactivities to run in parallel for a large generation job. Defaults to `4`.
- `outputDir`: (Optional) an optional path for the output data. Defaults to `.`.

---

## Possible updates to make

1. Add frame of reference modules.

## Possible features/improvements that require a lot of work:

1. Allow imaging studies for children and infants. Requires:
    1. Adding complexity to the patient age serialization.
    2. Makes adding weight/height to the DICOM more difficult
    3. Requires more complexity in deciding which protocols/modalities are appropriate for a patient (a 2 month old is not going to get a mammogram).
2. Remove hard-coding that each series lasts for 5 minutes. This requires research on how long all of the series modeled so far will last.
3. Adding instance-level specific fields.
4. Pixel data that represents the instances/series well.

## Physique Determination

From the document here <https://www.cdc.gov/nchs/data/series/sr_03/sr03_039.pdf,> we take the mean height for males at ages 16, 17, 18, 19 from table 7. From table 11 we take each of the ranges (20-29, 30-39, etc.) and assign that value to the midpoint to get mean heights at ages, 25, 35, ..., 75. Finally, from the "80 years and older" value, we make up some data points for ages 85, 95, 105. With all of these data points, we can create a spline interpolator for any age between 16-105. We repeat the same for females from the other tables. The spline interpolators give us a way to get a reasonable "reference height" for a person given sex + age.

1. First, for any given patient, they are given some "modifiers" representing individual physique deviations from the mean:
    1. The patient is assigned a "height modifier" *h* sampled from the normal distribution with mean 0 and variance 1 constrained to the range [-3, 3].
    2. The patient is assigned a "weight modifier" *w*. First, a value *b* is sampled from the beta distribution with *α = 2, β = 5*. Then, we assign *w = 10b - 2*. The beta distribution with the chosen parameters was selected to give the desired shape of the distribution. It allows for rarer but very large modifier values in the positive direction only. This is to model real life because you can have a patient who is very, very, heavy, but you can't have one who is 1.8 meters and 5 kilograms. The affine transformation after sampling from the distribution allows us to shift the range of values from [0, 1] to [-2, 8].
2. For a given study, we use the patient sex and age to determine the "reference height" *r* from the spline interpolation mentioned above.
3. Then, we assign the patient a size of *r + 0.05(h + d)*, where *d* is randomly selected between -0.5 and 0.5. This allows for reasonable variations in height while still maintaining the individual's height trajectory.
4. For weights, obtaining a reference weight is easier. From the chart at <https://www.nhs.uk/live-well/healthy-weight/height-weight-chart>, using a linear (affine) approximation between weight and height is reasonable. We take two points in the "overweight" category to determine our line: height = 1.5m, weight = 62.0kg and height = 2.0m, weight = 110.0kg. From that line, we have the patient's assigned height, so we can easily get a reference weight *g*.
5. Finally, similarly to before, we assign the final weight of *g + 10(w + n)* where again *n* is randomly selected between -0.5 and 0.5.
