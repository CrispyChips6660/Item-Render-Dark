package itemrender.export;

import java.util.Locale;

public enum ExportFormat
{
    STANDARD, MCMODCN;

    public static ExportFormat parse(String val)
    {
        switch (val.toLowerCase(Locale.ENGLISH))
        {
        case "standard":
            return STANDARD;
        default:
            return MCMODCN;
        }
    }
}
